package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.model.Parametros;
import com.example.happyplant.model.Rango;
import com.example.happyplant.utils.GPSHelper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.happyplant.model.Planta;
import com.example.happyplant.model.Usuario;
import com.example.happyplant.repository.UsuarioRepository;
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
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.eco_control);
        //+--------------------------------------------------------------------------------------------+

        txtGPS = findViewById(R.id.txtGPS);

        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });

        //boton de regresar
        ImageButton btnEcoControl_regresar = findViewById(R.id.btn_ecoControl_regresar);

        //spinner para la planta
        spinnerPlantas = findViewById(R.id.spinnerPlantas);
        spinnerWiki = findViewById(R.id.spinnerWiki);
        //Repositorio de usuarios
        usuarioRepo = new UsuarioRepository();

        // Sliders
        sliderAmbiental = findViewById(R.id.sliderAmbiental);
        sliderHumidity = findViewById(R.id.sliderHumidity);
        sliderTemperature = findViewById(R.id.sliderTemperature);

        // Etiquetas para mostrar rango actual
        labelAmbientalRange = findViewById(R.id.labelAmbientalRange);
        labelHumidityRange = findViewById(R.id.labelHumidityRange);
        labelTemperatureRange = findViewById(R.id.labelTemperatureRange);

        //carga el usuario logeado
        cargarUsuarioLogueado();

        // Detectar cambios en los sliders para actualizar texto en tiempo real
        configurarListenersDeSliders();
        // Cargar plantas Wiki precargadas
        cargarPlantasWiki();

        // Botón guardar
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> guardarCambios());
        //+--------------------------------------------------------------------------------------------+

        btnEcoControl_regresar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(ecoControl_activity.this, menu_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });
    }
    private void cargarUsuarioLogueado() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        String email = firebaseUser.getEmail(); // correo del usuario logueado

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

                                // Cargar sus plantas en el spinner
                                cargarSpinnerPlantas();
                            }
                        } else {
                            Toast.makeText(ecoControl_activity.this,
                                    "Usuario no encontrado",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("EcoControl", "Error al consultar usuario", error.toException());
                    }
                });
    }

    private void cargarSpinnerPlantas() {
        if (usuarioLogueado == null || usuarioLogueado.getPlantas() == null) {
            Toast.makeText(this, "No hay plantas para este usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Planta> listaPlantas = new ArrayList<>();
        List<String> nombresPlantas = new ArrayList<>();

        // Recorremos el Map de plantas (key = id de Firebase, value = Planta)
        for (Map.Entry<String, Planta> entry : usuarioLogueado.getPlantas().entrySet()) {
            Planta planta = entry.getValue();
            planta.setId(entry.getKey()); // <-- Asignamos el id de Firebase a la planta
            listaPlantas.add(planta);
            nombresPlantas.add(planta.getNombre());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombresPlantas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlantas.setAdapter(adapter);

        spinnerPlantas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Planta plantaSeleccionada = listaPlantas.get(position);
                actualizarSliders(plantaSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Opcional: limpiar sliders
            }
        });
    }

    private void cargarPlantasWiki() {
        listaPlantasWiki.clear();

        // Plantas precargadas con Humedad Ambiental
        listaPlantasWiki.add(crearPlantaWiki("Orquídea", 50f, 70f, 50f, 70f, 18f, 24f));
        listaPlantasWiki.add(crearPlantaWiki("Cactus Mini", 20f, 30f, 10f, 30f, 20f, 30f));
        listaPlantasWiki.add(crearPlantaWiki("Ficus Mini", 40f, 60f, 40f, 60f, 18f, 25f));
        listaPlantasWiki.add(crearPlantaWiki("Helecho Mini", 60f, 80f, 60f, 80f, 16f, 22f));
        listaPlantasWiki.add(crearPlantaWiki("Suculenta", 30f, 50f, 20f, 40f, 18f, 28f));
        listaPlantasWiki.add(crearPlantaWiki("Pilea", 40f, 60f, 40f, 60f, 18f, 24f));
        listaPlantasWiki.add(crearPlantaWiki("Espatifilo", 50f, 70f, 50f, 70f, 18f, 24f));
        listaPlantasWiki.add(crearPlantaWiki("Calatea", 60f, 80f, 60f, 80f, 18f, 24f));
        listaPlantasWiki.add(crearPlantaWiki("Hiedra", 40f, 60f, 40f, 60f, 16f, 22f));
        listaPlantasWiki.add(crearPlantaWiki("Bonsái", 30f, 50f, 40f, 60f, 15f, 25f));
        listaPlantasWiki.add(crearPlantaWiki("Zamioculca", 30f, 50f, 30f, 50f, 18f, 28f));
        listaPlantasWiki.add(crearPlantaWiki("Kalanchoe", 20f, 40f, 30f, 50f, 18f, 28f));
        listaPlantasWiki.add(crearPlantaWiki("Anthurium", 50f, 70f, 60f, 80f, 18f, 25f));
        listaPlantasWiki.add(crearPlantaWiki("Crotón", 40f, 60f, 50f, 70f, 18f, 26f));
        listaPlantasWiki.add(crearPlantaWiki("Fitonia", 50f, 70f, 60f, 80f, 18f, 24f));

        List<String> nombres = new ArrayList<>();
        for (Planta p : listaPlantasWiki) nombres.add(p.getNombre());

        ArrayAdapter<String> adapterWiki = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres);
        adapterWiki.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWiki.setAdapter(adapterWiki);

        spinnerWiki.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Planta plantaSeleccionada = listaPlantasWiki.get(position);
                actualizarSliders(plantaSeleccionada);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private Planta crearPlantaWiki(String nombre, double humAmbientalMin, double humAmbientalMax, double humSueloMin, double humSueloMax, double tempMin, double tempMax) {
        Planta p = new Planta();
        p.setNombre(nombre);

        Parametros param = new Parametros();
        // Solo humedad ambiental, humedad suelo y temperatura
        param.setRangoHumedadAmbiental(new Rango(null, humAmbientalMin, humAmbientalMax));
        param.setRangoHumedadSuelo(new Rango(null, humSueloMin, humSueloMax));
        param.setRangoTemperatura(new Rango(null, tempMin, tempMax));

        p.setParametros(param);
        return p;
    }
    private void actualizarSliders(Planta planta) {
        if (planta.getParametros() == null) return;

        // --- HUMEDAD AMBIENTAL ---
        if (planta.getParametros().getRangoHumedadAmbiental() != null) {
            float min = (float) planta.getParametros().getRangoHumedadAmbiental().getMinimo();
            float max = (float) planta.getParametros().getRangoHumedadAmbiental().getMaximo();

            // Validar antes de aplicarlo
            if (min < 0 || max > 100 || min > max) {
                Log.w("EcoControl", "⚠️ Valores fuera de rango en Humedad Ambiental: min=" + min + ", max=" + max);
                min = 0;
                max = 100;
            }

            // Primero definir rango permitido
            sliderAmbiental.setValueFrom(0);
            sliderAmbiental.setValueTo(100);

            // Luego aplicar los valores validados
            sliderAmbiental.setValues(min, max);
            labelAmbientalRange.setText(String.format("Min: %.0f%% - Max: %.0f%%", min, max));
        }

        // --- HUMEDAD DE SUELO ---
        if (planta.getParametros().getRangoHumedadSuelo() != null) {
            float min = (float) planta.getParametros().getRangoHumedadSuelo().getMinimo();
            float max = (float) planta.getParametros().getRangoHumedadSuelo().getMaximo();

            if (min < 0 || max > 1000 || min > max) {
                Log.w("EcoControl", "⚠️ Valores fuera de rango en Humedad Suelo: min=" + min + ", max=" + max);
                min = 0;
                max = 1000;
            }

            sliderHumidity.setValueFrom(0);
            sliderHumidity.setValueTo(1000);
            sliderHumidity.setValues(min, max);
            labelHumidityRange.setText(String.format("Min: %.0f%% - Max: %.0f%%", min, max));
        }

        // --- TEMPERATURA ---
        if (planta.getParametros().getRangoTemperatura() != null) {
            float min = (float) planta.getParametros().getRangoTemperatura().getMinimo();
            float max = (float) planta.getParametros().getRangoTemperatura().getMaximo();

            if (min < 0 || max > 50 || min > max) {
                Log.w("EcoControl", "⚠️ Valores fuera de rango en Temperatura: min=" + min + ", max=" + max);
                min = 0;
                max = 50;
            }

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
            return;
        }

        List<Planta> listaPlantas = new ArrayList<>(usuarioLogueado.getPlantas().values());
        Planta plantaSeleccionada = listaPlantas.get(pos);

        if (usuarioLogueado.getId() == null) {
            Toast.makeText(this, "Error: el ID del usuario es nulo", Toast.LENGTH_LONG).show();
            return;
        }

        if (plantaSeleccionada.getId() == null) {
            Toast.makeText(this, "Error: el ID de la planta es nulo", Toast.LENGTH_LONG).show();
            return;
        }

        // Valores de los sliders
        List<Float> ambiental = sliderAmbiental.getValues();
        List<Float> humedad = sliderHumidity.getValues();
        List<Float> temp = sliderTemperature.getValues();

        // Actualizar parámetros
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
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Parámetros ambientales actualizados correctamente", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }


}
