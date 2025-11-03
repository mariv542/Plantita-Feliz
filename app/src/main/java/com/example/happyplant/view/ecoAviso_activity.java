package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.happyplant.R;
import com.example.happyplant.model.Usuario;
import com.example.happyplant.repository.PlantaRepository;
import com.example.happyplant.utils.GPSHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ecoAviso_activity extends AppCompatActivity {

    private TextView txtGPS;
    private LinearLayout contenedorAlertas;
    private GPSHelper gpsHelper;

    private FirebaseAuth auth;
    private DatabaseReference usuarioRepo;
    private PlantaRepository plantaRepo;
    private Usuario usuarioLogueado;

    private Button btnHoy, btnPasado;
    private LinearLayout tabs;

    private static final String TAG = "EcoAviso";

    // Estado del filtro actual
    private boolean mostrandoHoy = true;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.eco_aviso);

        txtGPS = findViewById(R.id.txtGPS);
        contenedorAlertas = findViewById(R.id.contenedorAlertas);
        gpsHelper = new GPSHelper(this);

        btnHoy = findViewById(R.id.btnHoy);
        btnPasado = findViewById(R.id.btnPasado);
        tabs = findViewById(R.id.tabs);

        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });

        ImageButton btnEcoAviso_regresar = findViewById(R.id.btn_ecoAviso_regresar);
        btnEcoAviso_regresar.setOnClickListener(v -> {
            startActivity(new Intent(ecoAviso_activity.this, menu_activity.class));
        });

        auth = FirebaseAuth.getInstance();
        usuarioRepo = FirebaseDatabase.getInstance().getReference();

        // 👉 configurar listeners de los botones
        configurarTabs();

        // Cargar datos
        cargarUsuarioLogueado();
    }

    private void configurarTabs() {
        btnHoy.setOnClickListener(v -> {
            mostrandoHoy = true;
            btnHoy.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.lime_green));
            btnPasado.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
            cargarAlertasUsuario(usuarioLogueado != null ? usuarioLogueado.getId() : null);
        });

        btnPasado.setOnClickListener(v -> {
            mostrandoHoy = false;
            btnHoy.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
            btnPasado.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.orange_past));
            cargarAlertasUsuario(usuarioLogueado != null ? usuarioLogueado.getId() : null);
        });
    }

    private void cargarUsuarioLogueado() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "No hay sesión iniciada", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = firebaseUser.getEmail();

        usuarioRepo.child("usuarios")
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                usuarioLogueado = userSnap.getValue(Usuario.class);
                                usuarioLogueado.setId(userSnap.getKey());

                                plantaRepo = new PlantaRepository(usuarioLogueado.getId());
                                Toast.makeText(ecoAviso_activity.this, "Usuario cargado correctamente.", Toast.LENGTH_SHORT).show();

                                cargarAlertasUsuario(usuarioLogueado.getId());
                            }
                        } else {
                            Toast.makeText(ecoAviso_activity.this, "Usuario no encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error al consultar usuario", error.toException());
                    }
                });
    }

    private void cargarAlertasUsuario(String userId) {
        if (userId == null) return;

        DatabaseReference refPlantas = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(userId)
                .child("plantas");

        refPlantas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contenedorAlertas.removeAllViews();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String fechaHoy = sdf.format(Calendar.getInstance().getTime());

                for (DataSnapshot plantaSnap : snapshot.getChildren()) {
                    String nombrePlanta = plantaSnap.child("nombre").getValue(String.class);
                    DataSnapshot alertasSnap = plantaSnap.child("alertas");

                    if (alertasSnap.exists()) {
                        for (DataSnapshot alerta : alertasSnap.getChildren()) {
                            String mensaje = alerta.child("mensaje").getValue(String.class);
                            String fecha = alerta.child("fechaHora").getValue(String.class);
                            String nivel = alerta.child("nivel").getValue(String.class);

                            if (fecha == null || mensaje == null) continue;

                            // Filtrar según el tab
                            boolean esHoy = fecha.startsWith(fechaHoy);
                            if ((mostrandoHoy && !esHoy) || (!mostrandoHoy && esHoy))
                                continue;

                            TextView txt = new TextView(ecoAviso_activity.this);
                            txt.setText("🌿 " + nombrePlanta + "\n⚠️ " + mensaje + "\n📅 " + fecha);
                            txt.setPadding(20, 20, 20, 20);
                            txt.setTextSize(16);

                            if ("bajo".equalsIgnoreCase(nivel))
                                txt.setBackgroundColor(getResources().getColor(R.color.red_transparent));
                            else if ("medio".equalsIgnoreCase(nivel))
                                txt.setBackgroundColor(getResources().getColor(R.color.yellow_transparent));
                            else
                                txt.setBackgroundColor(getResources().getColor(R.color.green_transparent));

                            contenedorAlertas.addView(txt);
                        }
                    }
                }

                if (contenedorAlertas.getChildCount() == 0) {
                    TextView txt = new TextView(ecoAviso_activity.this);
                    txt.setText(mostrandoHoy ? "No hay alertas de hoy 🌞" : "No hay alertas pasadas 📅");
                    txt.setPadding(20, 20, 20, 20);
                    contenedorAlertas.addView(txt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error al leer alertas: " + error.getMessage());
            }
        });
    }
}
