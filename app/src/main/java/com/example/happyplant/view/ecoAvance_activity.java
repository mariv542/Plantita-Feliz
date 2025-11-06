package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ecoAvance_activity extends AppCompatActivity {
    private TextView txtGPS, txtNombrePlanta, txtTemperatura, txtHumedadSuelo, txtHumedadAmbiental;
    private Spinner spinnerPlantas;
    private ImageButton btnEcoAvanceRegresar;
    private LineChart chartAmbiente, chartSuelo;
    private Button btnFiltroAnio;
    private Button btnFiltroMes;
    private Button btnBuscar;
    private EditText inputFecha;
    private Planta plantaSeleccionada;
    private Button btnFiltroDia;
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

        // buttons busqueda
        btnFiltroDia = findViewById(R.id.btnFiltroDia);
        btnFiltroMes = findViewById(R.id.btnFiltroMes);
        btnFiltroAnio = findViewById(R.id.btnFiltroAnio);
        btnBuscar = findViewById(R.id.btnBuscar);
        inputFecha = findViewById(R.id.inputFecha);

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

        spinnerPlantas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Planta seleccionada y actualizar dashboard
                plantaSeleccionada = listaPlantas.get(position);
                actualizarDashboard(plantaSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Filtros de rango
        btnFiltroDia.setOnClickListener(v -> aplicarFiltro("dia"));
        btnFiltroMes.setOnClickListener(v -> aplicarFiltro("mes"));
        btnFiltroAnio.setOnClickListener(v -> aplicarFiltro("anio"));

        // Filtro manual por fecha ingresada
        btnBuscar.setOnClickListener(v -> {
            String fechaTexto = inputFecha.getText().toString().trim();
            if (fechaTexto.isEmpty()) {
                Toast.makeText(this, "Ingrese una fecha válida", Toast.LENGTH_SHORT).show();
                return;
            }
            aplicarFiltroPersonalizado(fechaTexto);
        });

        Log.d("EcoAvanceDBG", "btnFiltroDia == null? " + (btnFiltroDia == null));
        Log.d("EcoAvanceDBG", "btnFiltroMes == null? " + (btnFiltroMes == null));
        Log.d("EcoAvanceDBG", "btnFiltroAnio == null? " + (btnFiltroAnio == null));

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

        // Listener único
        spinnerPlantas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                plantaSeleccionada = listaPlantas.get(position); // asignamos correctamente
                actualizarDashboard(plantaSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Opcional: seleccionar la primera planta por defecto
        if (!listaPlantas.isEmpty()) {
            plantaSeleccionada = listaPlantas.get(0);
            actualizarDashboard(plantaSeleccionada);
        }
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

    private void aplicarFiltro(String tipo) {
        if (plantaSeleccionada == null) {
            Toast.makeText(this, "Seleccione una planta primero", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar limite = Calendar.getInstance();

        switch (tipo) {
            case "dia":
                limite.set(Calendar.HOUR_OF_DAY, 0);
                limite.set(Calendar.MINUTE, 0);
                limite.set(Calendar.SECOND, 0);
                limite.set(Calendar.MILLISECOND, 0);
                break;
            case "mes":
                limite.add(Calendar.MONTH, -1);
                break;
            case "anio":
                limite.add(Calendar.YEAR, -1);
                break;
        }

        filtrarYActualizar(plantaSeleccionada, limite.getTime());
    }

    private void aplicarFiltroPersonalizado(String fechaTexto) {
        try {
            Date fecha = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaTexto);
            filtrarYActualizar(plantaSeleccionada, fecha);
        } catch (Exception e) {
            Toast.makeText(this, "Formato inválido. Use dd/MM/yyyy", Toast.LENGTH_SHORT).show();
        }
    }

    private void filtrarYActualizar(Planta planta, Date fechaMinima) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());

        Map<String, Temperatura> tempFiltradas = new HashMap<>();
        Map<String, HumedadAmbiental> humAmbFiltradas = new HashMap<>();
        Map<String, HumedadSuelo> humSueloFiltradas = new HashMap<>();

        // 🔹 Temperaturas
        for (Map.Entry<String, Temperatura> entry : planta.getTemperaturas().entrySet()) {
            String fechaStr = entry.getValue().getFechaHora();
            if (fechaStr == null || fechaStr.trim().isEmpty()) {
                Log.w("EcoAvance", "Temperatura sin fecha, saltando registro");
                continue;
            }

            try {
                Date fecha = sdf.parse(fechaStr);
                if (fecha != null && !fecha.before(fechaMinima)) {
                    tempFiltradas.put(entry.getKey(), entry.getValue());
                }
            } catch (ParseException e) {
                Log.e("EcoAvance", "Error parseando fecha Temperatura: " + fechaStr);
            }
        }

        // 🔹 Humedad Ambiental
        for (Map.Entry<String, HumedadAmbiental> entry : planta.getHumedadesAmbientales().entrySet()) {
            String fechaStr = entry.getValue().getFechaHora();
            if (fechaStr == null || fechaStr.trim().isEmpty()) {
                Log.w("EcoAvance", "Humedad Ambiental sin fecha, saltando registro");
                continue;
            }

            try {
                Date fecha = sdf.parse(fechaStr);
                if (fecha != null && !fecha.before(fechaMinima)) {
                    humAmbFiltradas.put(entry.getKey(), entry.getValue());
                }
            } catch (ParseException e) {
                Log.e("EcoAvance", "Error parseando fecha Humedad Ambiental: " + fechaStr);
            }
        }

        // 🔹 Humedad Suelo
        for (Map.Entry<String, HumedadSuelo> entry : planta.getHumedadesSuelo().entrySet()) {
            String fechaStr = entry.getValue().getFechaHora();
            if (fechaStr == null || fechaStr.trim().isEmpty()) {
                Log.w("EcoAvance", "Humedad Suelo sin fecha, saltando registro");
                continue;
            }

            try {
                Date fecha = sdf.parse(fechaStr);
                if (fecha != null && !fecha.before(fechaMinima)) {
                    humSueloFiltradas.put(entry.getKey(), entry.getValue());
                }
            } catch (ParseException e) {
                Log.e("EcoAvance", "Error parseando fecha Humedad Suelo: " + fechaStr);
            }
        }

        // 🔹 Crear planta filtrada
        Planta plantaFiltrada = new Planta();
        plantaFiltrada.setNombre(planta.getNombre());
        plantaFiltrada.setTemperaturas(tempFiltradas);
        plantaFiltrada.setHumedadesAmbientales(humAmbFiltradas);
        plantaFiltrada.setHumedadesSuelo(humSueloFiltradas);

        // 🔹 Actualizar dashboard
        actualizarDashboard(plantaFiltrada);
    }


}
