package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.happyplant.utils.appLogger;
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

import java.text.SimpleDateFormat;
import java.util.*;

public class ecoAvance_activity extends AppCompatActivity {

    private TextView txtGPS, txtNombrePlanta, txtTemperatura, txtHumedadSuelo, txtHumedadAmbiental, txtNivelAgua;
    private Spinner spinnerPlantas;
    private ImageButton btnEcoAvanceRegresar;
    private LineChart chartAmbiente, chartSuelo;
    private Button btnFiltroAnio, btnFiltroMes, btnFiltroDia, btnBuscar;
    private EditText inputFecha;
    private Planta plantaSeleccionada;
    private GPSHelper gpsHelper;
    private Usuario usuarioLogueado;
    private List<Planta> listaPlantas = new ArrayList<>();
    private appLogger appLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_avance);

        // Inicializar logger
        String uid = "anonimo";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) uid = firebaseUser.getUid();
        appLogger = new appLogger(uid);
        appLogger.logEvent("abrirPantalla", "Usuario abrió ecoAvance_activity");

        // Inicializar vistas
        btnEcoAvanceRegresar = findViewById(R.id.btn_ecoAvance_regresar);
        spinnerPlantas = findViewById(R.id.spinnerPlantas);
        txtNombrePlanta = findViewById(R.id.txtNombrePlanta);
        txtTemperatura = findViewById(R.id.txtTemperatura);
        txtHumedadSuelo = findViewById(R.id.txtHumedadSuelo);
        txtHumedadAmbiental = findViewById(R.id.txtHumedadAmbiental);
        txtNivelAgua = findViewById(R.id.txtNivelAgua);
        txtGPS = findViewById(R.id.txtGPS);
        btnFiltroDia = findViewById(R.id.btnFiltroDia);
        btnFiltroMes = findViewById(R.id.btnFiltroMes);
        btnFiltroAnio = findViewById(R.id.btnFiltroAnio);
        btnBuscar = findViewById(R.id.btnBuscar);
        inputFecha = findViewById(R.id.inputFecha);
        chartAmbiente = findViewById(R.id.chartAmbiente);
        chartSuelo = findViewById(R.id.chartSuelo);

        setupCharts();

        // GPS
        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
            appLogger.logEvent("gpsObtenido", "Ciudad detectada: " + ciudad);
        });

        // Boton regresar
        btnEcoAvanceRegresar.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnEcoAvanceRegresar");
            startActivity(new Intent(ecoAvance_activity.this, menu_activity.class));
        });

        // Filtros de rango
        btnFiltroDia.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnFiltroDia");
            aplicarFiltro("dia");
        });
        btnFiltroMes.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnFiltroMes");
            aplicarFiltro("mes");
        });
        btnFiltroAnio.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnFiltroAnio");
            aplicarFiltro("anio");
        });

        // Filtro manual por fecha
        btnBuscar.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnBuscar");
            String fechaTexto = inputFecha.getText().toString().trim();
            if (fechaTexto.isEmpty()) {
                Toast.makeText(this, "Ingrese una fecha válida", Toast.LENGTH_SHORT).show();
                return;
            }
            aplicarFiltroPersonalizado(fechaTexto);
        });

        cargarUsuarioLogueado();
    }

    private void setupCharts() {
        configurarChart(chartAmbiente, "Temperatura y Humedad Ambiental");
        configurarChart(chartSuelo, "Humedad del Suelo");
    }

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
                                appLogger.logEvent("cargarUsuario", "Usuario logueado: " + usuarioLogueado.getEmail());
                                cargarPlantasUsuario(userSnap.child("plantas"));
                            }
                        } else {
                            Toast.makeText(ecoAvance_activity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                            appLogger.logEvent("errorUsuario", "No se encontró el usuario con email: " + email);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("EcoAvance", "Error al consultar usuario", error.toException());
                        appLogger.logEvent("errorBD", "Error al consultar usuario: " + error.getMessage());
                    }
                });
    }

    private void cargarPlantasUsuario(DataSnapshot plantasSnapshot) {
        listaPlantas.clear();
        if (!plantasSnapshot.exists()) {
            Toast.makeText(this, "No hay plantas para este usuario", Toast.LENGTH_SHORT).show();
            appLogger.logEvent("errorPlantas", "No hay plantas para este usuario");
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

        if (!listaPlantas.isEmpty()) {
            plantaSeleccionada = listaPlantas.get(0);
            actualizarDashboard(plantaSeleccionada);
        }

        appLogger.logEvent("cargarPlantas", "Plantas cargadas: " + listaPlantas.size());
    }

    private List<String> obtenerNombresPlantas(List<Planta> plantas) {
        List<String> nombres = new ArrayList<>();
        for (Planta p : plantas) nombres.add(p.getNombre());
        return nombres;
    }

    private void actualizarDashboard(Planta planta) {
        if (planta == null) return;
        txtNombrePlanta.setText(planta.getNombre());

        List<Entry> tempEntries = new ArrayList<>();
        List<Entry> humAmbEntries = new ArrayList<>();
        List<Entry> humSueloEntries = new ArrayList<>();
        List<Entry> nivelAguaEntries = new ArrayList<>();

        int i = 0;
        if (planta.getTemperaturas() != null) {
            for (Temperatura t : planta.getTemperaturas().values()) tempEntries.add(new Entry(i++, (float) t.getValor()));
        }
        i = 0;
        if (planta.getHumedadesAmbientales() != null) {
            for (HumedadAmbiental h : planta.getHumedadesAmbientales().values()) humAmbEntries.add(new Entry(i++, (float) h.getValor()));
        }
        i = 0;
        if (planta.getHumedadesSuelo() != null) {
            for (HumedadSuelo h : planta.getHumedadesSuelo().values()) humSueloEntries.add(new Entry(i++, (float) h.getValor()));
        }
        i = 0;
        if (planta.getNivelesAgua() != null) {
            for (NivelAgua n : planta.getNivelesAgua().values()) nivelAguaEntries.add(new Entry(i++, (float) n.getValor()));
        }

        LineDataSet setTemp = new LineDataSet(tempEntries, "Temperatura (°C)");
        setTemp.setColor(ContextCompat.getColor(this, R.color.red));
        setTemp.setCircleColor(ContextCompat.getColor(this, R.color.red));

        LineDataSet setHumAmb = new LineDataSet(humAmbEntries, "Humedad Ambiental (%)");
        setHumAmb.setColor(ContextCompat.getColor(this, R.color.blue));
        setHumAmb.setCircleColor(ContextCompat.getColor(this, R.color.blue));

        LineDataSet setHumSuelo = new LineDataSet(humSueloEntries, "Humedad del Suelo (%)");
        setHumSuelo.setColor(ContextCompat.getColor(this, R.color.green));
        setHumSuelo.setCircleColor(ContextCompat.getColor(this, R.color.green));

        LineDataSet setNivelAgua = new LineDataSet(nivelAguaEntries, "Nivel de Agua (%)");
        setNivelAgua.setColor(ContextCompat.getColor(this, R.color.red));
        setNivelAgua.setCircleColor(ContextCompat.getColor(this, R.color.red));

        chartAmbiente.setData(new LineData(setTemp, setHumAmb));
        chartSuelo.setData(new LineData(setHumSuelo, setNivelAgua));
        chartAmbiente.invalidate();
        chartSuelo.invalidate();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        try {
            if (planta.getTemperaturas() != null && !planta.getTemperaturas().isEmpty()) {
                Temperatura ultimaTemp = Collections.max(planta.getTemperaturas().values(),
                        Comparator.comparing(t -> {
                            try { return sdf.parse(t.getFecha()); } catch (Exception e) { return new Date(0); }
                        }));
                txtTemperatura.setText(String.format(Locale.getDefault(), "%.1f °C", ultimaTemp.getValor()));
            }

            if (planta.getHumedadesAmbientales() != null && !planta.getHumedadesAmbientales().isEmpty()) {
                HumedadAmbiental ultimaHumAmb = Collections.max(planta.getHumedadesAmbientales().values(),
                        Comparator.comparing(h -> {
                            try { return sdf.parse(h.getFecha()); } catch (Exception e) { return new Date(0); }
                        }));
                txtHumedadAmbiental.setText(String.format(Locale.getDefault(), "%.1f %%", ultimaHumAmb.getValor()));
            }

            if (planta.getHumedadesSuelo() != null && !planta.getHumedadesSuelo().isEmpty()) {
                HumedadSuelo ultimaHumSuelo = Collections.max(planta.getHumedadesSuelo().values(),
                        Comparator.comparing(h -> {
                            try { return sdf.parse(h.getFecha()); } catch (Exception e) { return new Date(0); }
                        }));
                txtHumedadSuelo.setText(String.format(Locale.getDefault(), "%.1f %%", ultimaHumSuelo.getValor()));
            }

            if (planta.getNivelesAgua() != null && !planta.getNivelesAgua().isEmpty()) {
                NivelAgua ultimoAgua = Collections.max(planta.getNivelesAgua().values(),
                        Comparator.comparing(n -> {
                            try { return sdf.parse(n.getFecha()); } catch (Exception e) { return new Date(0); }
                        }));
                txtNivelAgua.setText(String.format(Locale.getDefault(), "%.1f %%", ultimoAgua.getValor()));
            }
        } catch (Exception e) {
            Log.e("EcoAvance", "Error actualizando dashboard", e);
            appLogger.logEvent("errorDashboard", "Error actualizando dashboard: " + e.getMessage());
        }

        appLogger.logEvent("actualizarDashboard", "Dashboard actualizado para planta: " + planta.getNombre());
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

        appLogger.logEvent("aplicarFiltro", "Filtro aplicado: " + tipo);
        filtrarYActualizar(plantaSeleccionada, limite.getTime());
    }

    private void aplicarFiltroPersonalizado(String fechaTexto) {
        try {
            Date fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaTexto);
            appLogger.logEvent("aplicarFiltroPersonalizado", "Filtro personalizado aplicado: " + fechaTexto);
            filtrarYActualizar(plantaSeleccionada, fecha);
        } catch (Exception e) {
            Toast.makeText(this, "Formato inválido. Use dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            appLogger.logEvent("errorFiltroPersonalizado", "Formato inválido: " + fechaTexto);
        }
    }

    private void filtrarYActualizar(Planta planta, Date fechaMinima) {
        if (planta == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());

        Map<String, Temperatura> tempFiltradas = new HashMap<>();
        Map<String, HumedadAmbiental> humAmbFiltradas = new HashMap<>();
        Map<String, HumedadSuelo> humSueloFiltradas = new HashMap<>();
        Map<String, NivelAgua> nivelAguaFiltrados = new HashMap<>();

        if (planta.getTemperaturas() != null) {
            for (Map.Entry<String, Temperatura> entry : planta.getTemperaturas().entrySet()) {
                try {
                    Date fecha = sdf.parse(entry.getValue().getFecha());
                    if (fecha != null && !fecha.before(fechaMinima)) tempFiltradas.put(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    Log.e("EcoAvance", "Error parseando Temperatura", e);
                }
            }
        }

        if (planta.getHumedadesAmbientales() != null) {
            for (Map.Entry<String, HumedadAmbiental> entry : planta.getHumedadesAmbientales().entrySet()) {
                try {
                    Date fecha = sdf.parse(entry.getValue().getFecha());
                    if (fecha != null && !fecha.before(fechaMinima)) humAmbFiltradas.put(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    Log.e("EcoAvance", "Error parseando Humedad Ambiental", e);
                }
            }
        }

        if (planta.getHumedadesSuelo() != null) {
            for (Map.Entry<String, HumedadSuelo> entry : planta.getHumedadesSuelo().entrySet()) {
                try {
                    Date fecha = sdf.parse(entry.getValue().getFecha());
                    if (fecha != null && !fecha.before(fechaMinima)) humSueloFiltradas.put(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    Log.e("EcoAvance", "Error parseando Humedad Suelo", e);
                }
            }
        }

        if (planta.getNivelesAgua() != null) {
            for (Map.Entry<String, NivelAgua> entry : planta.getNivelesAgua().entrySet()) {
                try {
                    Date fecha = sdf.parse(entry.getValue().getFecha());
                    if (fecha != null && !fecha.before(fechaMinima)) nivelAguaFiltrados.put(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    Log.e("EcoAvance", "Error parseando Nivel Agua", e);
                }
            }
        }

        Planta plantaFiltrada = new Planta();
        plantaFiltrada.setNombre(planta.getNombre());
        plantaFiltrada.setTemperaturas(tempFiltradas);
        plantaFiltrada.setHumedadesAmbientales(humAmbFiltradas);
        plantaFiltrada.setHumedadesSuelo(humSueloFiltradas);
        plantaFiltrada.setNivelesAgua(nivelAguaFiltrados);

        appLogger.logEvent("filtrarYActualizar", "Filtrado completado → Temp: " + tempFiltradas.size()
                + " | HumAmb: " + humAmbFiltradas.size()
                + " | HumSuelo: " + humSueloFiltradas.size()
                + " | NivelAgua: " + nivelAguaFiltrados.size());

        // Actualizar dashboard con los datos filtrados
        actualizarDashboard(plantaFiltrada);
    }
}
