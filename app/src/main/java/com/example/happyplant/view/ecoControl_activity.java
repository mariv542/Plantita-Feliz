package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.model.Parametros;
import com.example.happyplant.model.Planta;
import com.example.happyplant.model.Rango;
import com.example.happyplant.model.Usuario;
import com.example.happyplant.repository.UsuarioRepository;
import com.example.happyplant.utils.GPSHelper;
import com.example.happyplant.utils.appLogger;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ecoControl_activity extends AppCompatActivity {

    private TextView txtGPS, labelAmbientalRange, labelHumidityRange, labelTemperatureRange;
    private GPSHelper gpsHelper;
    private Spinner spinnerPlantas, spinnerWiki;
    private UsuarioRepository usuarioRepo;
    private Button btnSave;
    private Usuario usuarioLogueado;
    private RangeSlider sliderAmbiental, sliderHumidity, sliderTemperature;

    private List<Planta> listaPlantasWiki = new ArrayList<>();
    private appLogger appLogger; // logger

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_control);

        // Inicializar appLogger con UID o "anonimo"
        String uid = "anonimo";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) uid = firebaseUser.getUid();
        appLogger = new appLogger(uid);
        appLogger.logEvent("abrirPantalla", "Usuario abrió ecoControl_activity");

        txtGPS = findViewById(R.id.txtGPS);
        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
            appLogger.logEvent("gpsObtenido", "Ciudad detectada: " + ciudad);
        });

        ImageButton btnEcoControl_regresar = findViewById(R.id.btn_ecoControl_regresar);

        spinnerPlantas = findViewById(R.id.spinnerPlantas);
        spinnerWiki = findViewById(R.id.spinnerWiki);

        usuarioRepo = new UsuarioRepository();

        sliderAmbiental = findViewById(R.id.sliderAmbiental);
        sliderHumidity = findViewById(R.id.sliderHumidity);
        sliderTemperature = findViewById(R.id.sliderTemperature);

        labelAmbientalRange = findViewById(R.id.labelAmbientalRange);
        labelHumidityRange = findViewById(R.id.labelHumidityRange);
        labelTemperatureRange = findViewById(R.id.labelTemperatureRange);

        // Cargar usuario logueado
        cargarUsuarioLogueado();

        // Detectar cambios en sliders
        configurarListenersDeSliders();

        // Cargar plantas Wiki precargadas
        cargarPlantasWiki();

        // Botón guardar
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> guardarCambios());

        // Botón regresar
        btnEcoControl_regresar.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnEcoControl_regresar");
            startActivity(new Intent(ecoControl_activity.this, menu_activity.class));
        });
    }

    private void cargarUsuarioLogueado() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        String email = firebaseUser.getEmail();
        usuarioRepo.getReference("usuarios")
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                usuarioLogueado = userSnap.getValue(Usuario.class);
                                usuarioLogueado.setId(userSnap.getKey());
                                appLogger.logEvent("cargarUsuario", "Usuario logueado: " + usuarioLogueado.getEmail());

                                // Cargar sus plantas en el spinner
                                cargarSpinnerPlantas();
                                break;
                            }
                        } else {
                            Toast.makeText(ecoControl_activity.this,
                                    "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                            appLogger.logEvent("errorUsuario", "No se encontró usuario con email: " + email);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("EcoControl", "Error al consultar usuario", error.toException());
                        appLogger.logEvent("errorBD", "Error al consultar usuario: " + error.getMessage());
                    }
                });
    }

    private void cargarSpinnerPlantas() {
        if (usuarioLogueado == null || usuarioLogueado.getPlantas() == null) {
            Toast.makeText(this, "No hay plantas para este usuario", Toast.LENGTH_SHORT).show();
            appLogger.logEvent("errorUsuario", "No hay plantas para este usuario");
            return;
        }

        List<Planta> listaPlantas = new ArrayList<>();
        List<String> nombresPlantas = new ArrayList<>();
        for (Map.Entry<String, Planta> entry : usuarioLogueado.getPlantas().entrySet()) {
            Planta planta = entry.getValue();
            planta.setId(entry.getKey());
            listaPlantas.add(planta);
            nombresPlantas.add(planta.getNombre());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresPlantas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlantas.setAdapter(adapter);

        spinnerPlantas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Planta plantaSeleccionada = listaPlantas.get(position);
                actualizarSliders(plantaSeleccionada);
                appLogger.logEvent("seleccionPlanta", "Seleccionó planta: " + plantaSeleccionada.getNombre());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarPlantasWiki() {
        listaPlantasWiki.clear();
        listaPlantasWiki.add(crearPlantaWiki("Orquídea", 50f, 70f, 50f, 70f, 18f, 24f));
        listaPlantasWiki.add(crearPlantaWiki("Cactus Mini", 20f, 30f, 10f, 30f, 20f, 30f));
        listaPlantasWiki.add(crearPlantaWiki("Ficus Mini", 40f, 60f, 40f, 60f, 18f, 25f));
        // ... más plantas precargadas
        List<String> nombres = new ArrayList<>();
        for (Planta p : listaPlantasWiki) nombres.add(p.getNombre());

        ArrayAdapter<String> adapterWiki = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        adapterWiki.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWiki.setAdapter(adapterWiki);

        spinnerWiki.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Planta plantaSeleccionada = listaPlantasWiki.get(position);
                actualizarSliders(plantaSeleccionada);
                appLogger.logEvent("seleccionPlantaWiki", "Seleccionó planta Wiki: " + plantaSeleccionada.getNombre());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private Planta crearPlantaWiki(String nombre, double humAmbientalMin, double humAmbientalMax,
                                   double humSueloMin, double humSueloMax, double tempMin, double tempMax) {
        Planta p = new Planta();
        p.setNombre(nombre);
        Parametros param = new Parametros();
        param.setRangoHumedadAmbiental(new Rango(null, humAmbientalMin, humAmbientalMax));
        param.setRangoHumedadSuelo(new Rango(null, humSueloMin, humSueloMax));
        param.setRangoTemperatura(new Rango(null, tempMin, tempMax));
        p.setParametros(param);
        return p;
    }

    private void actualizarSliders(Planta planta) {
        if (planta.getParametros() == null) return;

        // Humedad ambiental
        if (planta.getParametros().getRangoHumedadAmbiental() != null) {
            float min = (float) planta.getParametros().getRangoHumedadAmbiental().getMinimo();
            float max = (float) planta.getParametros().getRangoHumedadAmbiental().getMaximo();
            if (min < 0 || max > 100 || min > max) { min = 0; max = 100; }
            sliderAmbiental.setValueFrom(0);
            sliderAmbiental.setValueTo(100);
            sliderAmbiental.setValues(min, max);
            labelAmbientalRange.setText(String.format("Min: %.0f%% - Max: %.0f%%", min, max));
        }

        // Humedad suelo
        if (planta.getParametros().getRangoHumedadSuelo() != null) {
            float min = (float) planta.getParametros().getRangoHumedadSuelo().getMinimo();
            float max = (float) planta.getParametros().getRangoHumedadSuelo().getMaximo();
            if (min < 0 || max > 1000 || min > max) { min = 0; max = 1000; }
            sliderHumidity.setValueFrom(0);
            sliderHumidity.setValueTo(1000);
            sliderHumidity.setValues(min, max);
            labelHumidityRange.setText(String.format("Min: %.0f%% - Max: %.0f%%", min, max));
        }

        // Temperatura
        if (planta.getParametros().getRangoTemperatura() != null) {
            float min = (float) planta.getParametros().getRangoTemperatura().getMinimo();
            float max = (float) planta.getParametros().getRangoTemperatura().getMaximo();
            if (min < 0 || max > 50 || min > max) { min = 0; max = 50; }
            sliderTemperature.setValueFrom(0);
            sliderTemperature.setValueTo(50);
            sliderTemperature.setValues(min, max);
            labelTemperatureRange.setText(String.format("Min: %.0f°C - Max: %.0f°C", min, max));
        }
    }

    private void configurarListenersDeSliders() {
        sliderAmbiental.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> vals = slider.getValues();
            labelAmbientalRange.setText("Min: " + vals.get(0) + "%   -   Max: " + vals.get(1) + "%");
        });
        sliderHumidity.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> vals = slider.getValues();
            labelHumidityRange.setText("Min: " + vals.get(0) + "%   -   Max: " + vals.get(1) + "%");
        });
        sliderTemperature.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> vals = slider.getValues();
            labelTemperatureRange.setText("Min: " + vals.get(0) + "°C   -   Max: " + vals.get(1) + "°C");
        });
    }

    private void guardarCambios() {
        int pos = spinnerPlantas.getSelectedItemPosition();
        if (usuarioLogueado == null || usuarioLogueado.getPlantas() == null) {
            Toast.makeText(this, "No hay plantas para actualizar", Toast.LENGTH_SHORT).show();
            appLogger.logEvent("errorGuardar", "No hay plantas para actualizar");
            return;
        }

        List<Planta> listaPlantas = new ArrayList<>(usuarioLogueado.getPlantas().values());
        Planta plantaSeleccionada = listaPlantas.get(pos);

        if (usuarioLogueado.getId() == null || plantaSeleccionada.getId() == null) {
            Toast.makeText(this, "Error: IDs nulos", Toast.LENGTH_LONG).show();
            appLogger.logEvent("errorGuardar", "ID usuario o planta nulo");
            return;
        }

        List<Float> ambiental = sliderAmbiental.getValues();
        List<Float> humedad = sliderHumidity.getValues();
        List<Float> temp = sliderTemperature.getValues();

        plantaSeleccionada.getParametros().getRangoHumedadAmbiental().setMinimo(ambiental.get(0));
        plantaSeleccionada.getParametros().getRangoHumedadAmbiental().setMaximo(ambiental.get(1));
        plantaSeleccionada.getParametros().getRangoHumedadSuelo().setMinimo(humedad.get(0));
        plantaSeleccionada.getParametros().getRangoHumedadSuelo().setMaximo(humedad.get(1));
        plantaSeleccionada.getParametros().getRangoTemperatura().setMinimo(temp.get(0));
        plantaSeleccionada.getParametros().getRangoTemperatura().setMaximo(temp.get(1));

        usuarioRepo.getReference("usuarios")
                .child(usuarioLogueado.getId())
                .child("plantas")
                .child(plantaSeleccionada.getId())
                .setValue(plantaSeleccionada)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Parámetros ambientales actualizados correctamente", Toast.LENGTH_SHORT).show();
                    appLogger.logEvent("guardarCambios", "Actualizó planta: " + plantaSeleccionada.getNombre());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    appLogger.logEvent("errorGuardar", "Error al guardar: " + e.getMessage());
                });
    }
}
