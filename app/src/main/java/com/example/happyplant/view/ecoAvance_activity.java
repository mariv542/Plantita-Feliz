package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.model.*;
import com.example.happyplant.utils.GPSHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.*;

public class ecoAvance_activity extends AppCompatActivity {
    private TextView txtGPS, txtNombrePlanta, txtTemperatura, txtHumedadSuelo, txtHumedadAmbiental;
    private Spinner spinnerPlantas;
    private ImageButton btnEcoAvanceRegresar;
    private LineChart chartAmbiente, chartSuelo;

    private GPSHelper gpsHelper;
    private Usuario usuarioLogueado;
    private List<Planta> listaPlantas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.eco_avance);

        //+-------------------------------------------------------------------------------------------+

        // Inicializar vistas
        btnEcoAvanceRegresar = findViewById(R.id.btn_ecoAvance_regresar);
        spinnerPlantas = findViewById(R.id.spinnerPlantas);
        txtNombrePlanta = findViewById(R.id.txtNombrePlanta);
        txtTemperatura = findViewById(R.id.txtTemperatura);
        txtHumedadSuelo = findViewById(R.id.txtHumedadSuelo);
        txtHumedadAmbiental = findViewById(R.id.txtHumedadAmbiental);
        txtGPS = findViewById(R.id.txtGPS);

        // Nuevos graficos
        chartAmbiente = findViewById(R.id.chartAmbiente);
        chartSuelo = findViewById(R.id.chartSuelo);

        //+-------------------------------------------------------------------------------------------+

        setupCharts();

        // GPS
        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });

        // Boton regresar
        btnEcoAvanceRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(ecoAvance_activity.this, menu_activity.class);
            startActivity(intent);
        });

        // Cargar usuario
        cargarUsuarioLogueado();

        //+-------------------------------------------------------------------------------------------+
    }

    // metodos
    private void setupCharts() {
        configurarChart(chartAmbiente, "Temperatura y Humedad Ambiental");
        configurarChart(chartSuelo, "Humedad del Suelo");
    }

    //+-------------------------------------------------------------------------------------------+
    private void configurarChart(LineChart chart, String descripcion) {
        chart.getDescription().setText(descripcion);
        chart.getDescription().setTextSize(12f);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawGridLines(true);

        Legend legend = chart.getLegend();
        legend.setEnabled(true);
    }

    //+-------------------------------------------------------------------------------------------+
    private void cargarUsuarioLogueado() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        String email = firebaseUser.getEmail();
        DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
        refUsuarios.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                usuarioLogueado = userSnap.getValue(Usuario.class);
                                usuarioLogueado.setId(userSnap.getKey());
                                cargarPlantasUsuario(userSnap.child("plantas"));
                            }
                        } else {
                            Toast.makeText(ecoAvance_activity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("EcoAvance", "Error al consultar usuario", error.toException());
                    }
                });
    }

    //+-------------------------------------------------------------------------------------------+
    private void cargarPlantasUsuario(DataSnapshot plantasSnapshot) {
        listaPlantas.clear();
        if (!plantasSnapshot.exists()) {
            Toast.makeText(this, "No hay plantas para este usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        for (DataSnapshot ds : plantasSnapshot.getChildren()) {
            Planta planta = ds.getValue(Planta.class);
            if (planta != null) {
                planta.setId(ds.getKey());
                planta.cargarDatosDesdeSnapshot(ds);
                listaPlantas.add(planta);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                obtenerNombresPlantas(listaPlantas)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlantas.setAdapter(adapter);

        spinnerPlantas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarDashboard(listaPlantas.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    //+-------------------------------------------------------------------------------------------+
    private List<String> obtenerNombresPlantas(List<Planta> plantas) {
        List<String> nombres = new ArrayList<>();
        for (Planta p : plantas) nombres.add(p.getNombre());
        return nombres;
    }

    //+-------------------------------------------------------------------------------------------+
    private void actualizarDashboard(Planta planta) {
        txtNombrePlanta.setText(planta.getNombre());

        // 🔹 Datos de Firebase
        List<Entry> tempEntries = new ArrayList<>();
        List<Entry> humAmbEntries = new ArrayList<>();
        List<Entry> humSueloEntries = new ArrayList<>();

        int i = 0;
        for (Temperatura t : planta.getTemperaturas().values()) {
            tempEntries.add(new Entry(i++, (float) t.getValor()));
        }
        i = 0;
        for (HumedadAmbiental h : planta.getHumedadesAmbientales().values()) {
            humAmbEntries.add(new Entry(i++, (float) h.getValor()));
        }
        i = 0;
        for (HumedadSuelo h : planta.getHumedadesSuelo().values()) {
            humSueloEntries.add(new Entry(i++, (float) h.getValor()));
        }

        //+-------------------------------------------------------------------------------------------+
        // DataSets
        LineDataSet setTemp = new LineDataSet(tempEntries, "Temperatura (°C)");
        setTemp.setColor(ContextCompat.getColor(this, R.color.red));
        setTemp.setCircleColor(ContextCompat.getColor(this, R.color.red));

        LineDataSet setHumAmb = new LineDataSet(humAmbEntries, "Humedad Ambiental (%)");
        setHumAmb.setColor(ContextCompat.getColor(this, R.color.blue));
        setHumAmb.setCircleColor(ContextCompat.getColor(this, R.color.blue));

        LineDataSet setHumSuelo = new LineDataSet(humSueloEntries, "Humedad del Suelo (%)");
        setHumSuelo.setColor(ContextCompat.getColor(this, R.color.green));
        setHumSuelo.setCircleColor(ContextCompat.getColor(this, R.color.green));

        // Mostrar en los graficos
        chartAmbiente.setData(new LineData(setTemp, setHumAmb));
        chartSuelo.setData(new LineData(setHumSuelo));

        chartAmbiente.invalidate();
        chartSuelo.invalidate();

        // Actualizar valores actuales
        if (!tempEntries.isEmpty())
            txtTemperatura.setText(tempEntries.get(tempEntries.size() - 1).getY() + " °C");
        if (!humAmbEntries.isEmpty())
            txtHumedadAmbiental.setText(humAmbEntries.get(humAmbEntries.size() - 1).getY() + " %");
        if (!humSueloEntries.isEmpty())
            txtHumedadSuelo.setText(humSueloEntries.get(humSueloEntries.size() - 1).getY() + " %");
    }
}
