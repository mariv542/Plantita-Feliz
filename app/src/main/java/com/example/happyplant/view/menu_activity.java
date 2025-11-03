package com.example.happyplant.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.model.HumedadSuelo;
import com.example.happyplant.model.Planta;
import com.example.happyplant.model.Rango;
import com.example.happyplant.model.Usuario;
import com.example.happyplant.utils.GPSHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class menu_activity extends AppCompatActivity {

    private TextView txtGPS;
    private GPSHelper gpsHelper;
    private Usuario usuarioLogueado;
    private ImageView imgPlanta;
    private ImageButton btnMenu_perfil,btnMenu_ecoControl,btnMenu_ecoPlanta,btnMenu_ecoAviso,btnMenu_ecoAvance ;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.menu);
        //+--------------------------------------------------------------------------------------------+

        btnMenu_ecoAvance = findViewById(R.id.btn_menu_ecoAvance);
        btnMenu_ecoAviso = findViewById(R.id.btn_menu_ecoAviso);
        btnMenu_ecoPlanta = findViewById(R.id.btn_menu_ecoPlanta);
        btnMenu_ecoControl = findViewById(R.id.btn_menu_ecoControl);
        btnMenu_perfil = findViewById(R.id.btn_menu_perfil);
        imgPlanta = findViewById(R.id.imgHumedad);
        // Notificaciones
        verificarPermisoNotificaciones();
        cargarUsuarioLogueado();



        //Para GPS
        txtGPS = findViewById(R.id.txtGPS);

        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });
        //+--------------------------------------------------------------------------------------------+

        btnMenu_ecoAvance.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(menu_activity.this, ecoAvance_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });

        btnMenu_ecoAviso.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(menu_activity.this, ecoAviso_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });

        btnMenu_ecoPlanta.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(menu_activity.this, ecoPlanta_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });

        btnMenu_ecoControl.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(menu_activity.this, ecoControl_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });

        btnMenu_perfil.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(menu_activity.this, perfil_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });
    }

    private void verificarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }
    private void cargarUsuarioLogueado() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        String email = firebaseUser.getEmail();
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");

        usuariosRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                usuarioLogueado = userSnap.getValue(Usuario.class);
                                usuarioLogueado.setId(userSnap.getKey());

                                // Cargar planta del usuario
                                cargarPlantaUsuario();
                                break;
                            }
                        } else {
                            Toast.makeText(menu_activity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("MenuActivity", "Error al consultar usuario", error.toException());
                    }
                });
    }
    private void cargarPlantaUsuario() {
        if (usuarioLogueado == null) return;

        DatabaseReference plantasRef = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(usuarioLogueado.getId())
                .child("plantas");

        plantasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Planta> mapPlantas = new HashMap<>();

                    for (DataSnapshot plantaSnap : snapshot.getChildren()) {
                        Planta planta = plantaSnap.getValue(Planta.class);
                        if (planta == null) continue;
                        planta.setId(plantaSnap.getKey());
                        planta.cargarDatosDesdeSnapshot(plantaSnap);

                        mapPlantas.put(planta.getId(), planta);
                    }

                    usuarioLogueado.setPlantas(mapPlantas);

                    // Seleccionar la primera planta automáticamente
                    if (!mapPlantas.isEmpty()) {
                        Planta primeraPlanta = mapPlantas.values().iterator().next();

                        if (primeraPlanta.getHumedadesSuelo() != null && !primeraPlanta.getHumedadesSuelo().isEmpty()) {
                            Map.Entry<String, HumedadSuelo> entrada =
                                    primeraPlanta.getHumedadesSuelo().entrySet().iterator().next();
                            double humedadActual = entrada.getValue().getValor();
                            actualizarImagenHumedad(humedadActual, primeraPlanta);
                        }
                    }
                } else {
                    Toast.makeText(menu_activity.this, "No hay plantas registradas para este usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MenuActivity", "Error al cargar planta", error.toException());
            }
        });
    }
        private void actualizarImagenHumedad(double humedadActual, Planta planta) {
            if (planta == null || planta.getParametros() == null || planta.getParametros().getRangoHumedadSuelo() == null) return;

            Rango rango = planta.getParametros().getRangoHumedadSuelo();

            if (humedadActual < rango.getMinimo()) {
                imgPlanta.setImageResource(R.drawable.planta_normal); // Falta humedad
            } else if (humedadActual > rango.getMaximo()) {
                imgPlanta.setImageResource(R.drawable.planta_triste); // Exceso de humedad
            } else {
                imgPlanta.setImageResource(R.drawable.planta_feliz); // Dentro del rango
            }
        }

    }


