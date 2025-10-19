package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
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

    private TextView txtGPS, labelWaterRange, labelHumidityRange, labelTemperatureRange;
    private GPSHelper gpsHelper;
    private Spinner spinnerPlantas;
    private UsuarioRepository usuarioRepo;
    private Button btnSave;
    private Usuario usuarioLogueado;
    private RangeSlider sliderWater, sliderHumidity, sliderTemperature;
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
        //Repositorio de usuarios
        usuarioRepo = new UsuarioRepository();

        // Sliders
        sliderWater = findViewById(R.id.sliderWater);
        sliderHumidity = findViewById(R.id.sliderHumidity);
        sliderTemperature = findViewById(R.id.sliderTemperature);
        // Etiquetas para mostrar rango actual
        labelWaterRange = findViewById(R.id.labelWaterRange);
        labelHumidityRange = findViewById(R.id.labelHumidityRange);
        labelTemperatureRange = findViewById(R.id.labelTemperatureRange);

        //carga el usuario logeado
        cargarUsuarioLogueado();

        // Detectar cambios en los sliders para actualizar texto en tiempo real
        configurarListenersDeSliders();

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


    private void actualizarSliders(Planta planta) {
        if (planta.getParametros() == null) return;

        // Agua
        if (planta.getParametros().getRangoNivelAgua() != null) {
            float min = (float) planta.getParametros().getRangoNivelAgua().getMinimo();
            float max = (float) planta.getParametros().getRangoNivelAgua().getMaximo();
            sliderWater.setValues(min, max);
            sliderWater.setValueFrom(0);
            sliderWater.setValueTo(max + 10);
            labelWaterRange.setText("Min: " + min + " ml   -   Max: " + max + " ml");
        }

        // Humedad
        if (planta.getParametros().getRangoHumedadSuelo() != null) {
            float min = (float) planta.getParametros().getRangoHumedadSuelo().getMinimo();
            float max = (float) planta.getParametros().getRangoHumedadSuelo().getMaximo();
            sliderHumidity.setValues(min, max);
            sliderHumidity.setValueFrom(0);
            sliderHumidity.setValueTo(100);
            labelHumidityRange.setText("Min: " + min + "%   -   Max: " + max + "%");
        }

        // Temperatura
        if (planta.getParametros().getRangoTemperatura() != null) {
            float min = (float) planta.getParametros().getRangoTemperatura().getMinimo();
            float max = (float) planta.getParametros().getRangoTemperatura().getMaximo();
            sliderTemperature.setValues(min, max);
            sliderTemperature.setValueFrom(0);
            sliderTemperature.setValueTo(50);
            labelTemperatureRange.setText("Min: " + min + "°C   -   Max: " + max + "°C");
        }
    }

    private void configurarListenersDeSliders() {
        sliderWater.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> vals = slider.getValues();
            labelWaterRange.setText("Min: " + vals.get(0) + " ml   -   Max: " + vals.get(1) + " ml");
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

        List<Float> agua = sliderWater.getValues();
        List<Float> humedad = sliderHumidity.getValues();
        List<Float> temp = sliderTemperature.getValues();

        plantaSeleccionada.getParametros().getRangoNivelAgua().setMinimo(agua.get(0));
        plantaSeleccionada.getParametros().getRangoNivelAgua().setMaximo(agua.get(1));
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
                        Toast.makeText(this, "Parámetros actualizados correctamente", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }


}
