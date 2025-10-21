package com.example.happyplant.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.model.HumedadAmbiental;
import com.example.happyplant.model.HumedadSuelo;
import com.example.happyplant.model.Parametros;
import com.example.happyplant.model.Planta;
import com.example.happyplant.model.Rango;
import com.example.happyplant.model.Temperatura;
import com.example.happyplant.model.Usuario;
import com.example.happyplant.utils.GPSHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ecoAvance_activity extends AppCompatActivity {
    // para GPS
    private TextView txtGPS;
    private GPSHelper gpsHelper;
    //UI
    private Spinner spinnerPlantas;
    private ImageView chartView;
    private TextView txtNombrePlanta, txtTemperatura, txtHumedadSuelo, txtHumedadAmbiental;
    private ImageButton btnEcoAvanceRegresar;
    // Firebase
    private Usuario usuarioLogueado;

    private List<Planta> listaPlantas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.eco_avance);
        //+--------------------------------------------------------------------------------------------+
            //inicializar vistas
        btnEcoAvanceRegresar = findViewById(R.id.btn_ecoAvance_regresar);
        spinnerPlantas = findViewById(R.id.spinnerPlantas);
        chartView = findViewById(R.id.chartView);
        txtNombrePlanta = findViewById(R.id.txtNombrePlanta);
        txtTemperatura = findViewById(R.id.txtTemperatura);
        txtHumedadSuelo = findViewById(R.id.txtHumedadSuelo);
        txtHumedadAmbiental = findViewById(R.id.txtHumedadAmbiental);

        //Para GPS
        txtGPS = findViewById(R.id.txtGPS);

        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });

        //+--------------------------------------------------------------------------------------------+


        // Cargar datos del usuario logueado
        cargarUsuarioLogueado();

        //para boton de  regresar
        btnEcoAvanceRegresar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(ecoAvance_activity.this, menu_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });
    }

    //metodos

    private void cargarUsuarioLogueado() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        String email = firebaseUser.getEmail(); // correo actual del usuario

        // Referencia a usuarios en Firebase
        DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
        refUsuarios.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                // Cargar datos del usuario
                                usuarioLogueado = userSnap.getValue(Usuario.class);
                                usuarioLogueado.setId(userSnap.getKey());
                                Log.d("EcoAvance", "Usuario logueado: " + usuarioLogueado.getNombre());

                                // Cargar sus plantas en la lista
                                cargarPlantasUsuario(userSnap.child("plantas"));
                            }
                        } else {
                            Toast.makeText(ecoAvance_activity.this,
                                    "Usuario no encontrado en la base de datos",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("EcoAvance", "Error al consultar usuario", error.toException());
                    }
                });
    }

    // Obtener nombres de plantas para el spinner
    private List<String> obtenerNombresPlantas(List<Planta> plantas) {
        List<String> nombres = new ArrayList<>();
        for (Planta p : plantas) {
            nombres.add(p.getNombre() != null ? p.getNombre() : "Sin nombre");
        }
        return nombres;
    }

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

                // Cargar temperaturas como Map
                if (ds.child("temperaturas").exists()) {
                    Map<String, Temperatura> temps = new HashMap<>();
                    for (DataSnapshot tempSnap : ds.child("temperaturas").getChildren()) {
                        Temperatura t = tempSnap.getValue(Temperatura.class);
                        if (t != null) temps.put(tempSnap.getKey(), t);
                    }
                    planta.setTemperaturas(temps);
                }

                // Cargar humedades de suelo
                if (ds.child("humedadesSuelo").exists()) {
                    Map<String, HumedadSuelo> humSueloMap = new HashMap<>();
                    for (DataSnapshot humSnap : ds.child("humedadesSuelo").getChildren()) {
                        HumedadSuelo h = humSnap.getValue(HumedadSuelo.class);
                        if (h != null) humSueloMap.put(humSnap.getKey(), h);
                    }
                    planta.setHumedadesSuelo(humSueloMap);
                }

                // Cargar humedades ambientales
                if (ds.child("humedadesAmbientales").exists()) {
                    Map<String, HumedadAmbiental> humAmbMap = new HashMap<>();
                    for (DataSnapshot humAmbSnap : ds.child("humedadesAmbientales").getChildren()) {
                        HumedadAmbiental h = humAmbSnap.getValue(HumedadAmbiental.class);
                        if (h != null) humAmbMap.put(humAmbSnap.getKey(), h);
                    }
                    planta.setHumedadesAmbientales(humAmbMap);
                }

                listaPlantas.add(planta);
            }
        }

        // Llenar el spinner con los nombres de plantas
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
                Planta planta = listaPlantas.get(position);
                actualizarDashboard(planta);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    public void actualizarDashboard(Planta planta) {
        if (planta == null) return;

        txtNombrePlanta.setText(planta.getNombre());

        double temp = 0, humSuelo = 0, humAmb = 0;

        // Temperatura
        if (planta.getTemperaturas() != null && !planta.getTemperaturas().isEmpty()) {
            Temperatura ultimaTemp = planta.getTemperaturas()
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .reduce((first, second) -> second)
                    .orElse(null);
            if (ultimaTemp != null) temp = ultimaTemp.getValor();
        }

        // Humedad del suelo
        if (planta.getHumedadesSuelo() != null && !planta.getHumedadesSuelo().isEmpty()) {
            HumedadSuelo ultimaHumSuelo = planta.getHumedadesSuelo()
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .reduce((first, second) -> second)
                    .orElse(null);
            if (ultimaHumSuelo != null) humSuelo = ultimaHumSuelo.getValor();
        }

        // Humedad ambiental
        if (planta.getHumedadesAmbientales() != null && !planta.getHumedadesAmbientales().isEmpty()) {
            HumedadAmbiental ultimaHumAmb = planta.getHumedadesAmbientales()
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .reduce((first, second) -> second)
                    .orElse(null);
            if (ultimaHumAmb != null) humAmb = ultimaHumAmb.getValor();
        }

        txtTemperatura.setText(temp + " °C");
        txtHumedadSuelo.setText(humSuelo + " %");
        txtHumedadAmbiental.setText(humAmb + " %");

        // Dibujar dashboard
        int ancho = chartView.getWidth();
        int alto = chartView.getHeight();
        if (ancho == 0 || alto == 0) return;

        Bitmap bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(40);
        paint.setColor(Color.BLACK);

        canvas.drawColor(Color.parseColor("#E0F2F1"));
        canvas.drawText("Temperatura: " + temp + " °C", 50, 80, paint);
        canvas.drawText("Humedad del Suelo: " + humSuelo + " %", 50, 150, paint);
        canvas.drawText("Humedad Ambiental: " + humAmb + " %", 50, 220, paint);

        chartView.setImageBitmap(bitmap);
    }
}
