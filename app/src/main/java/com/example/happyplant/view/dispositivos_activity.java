package com.example.happyplant.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happyplant.Adapter.PlantasAdapter;
import com.example.happyplant.R;
import com.example.happyplant.model.Planta;
import com.example.happyplant.utils.GPSHelper;
import com.example.happyplant.utils.appLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class dispositivos_activity extends AppCompatActivity {
    // para GPS
    private TextView txtGPS;
    private GPSHelper gpsHelper;
    private RecyclerView recyclerPlantas;
    private TextView txtCantidadPlantas;
    private ArrayList<Planta> listaPlantas;
    private ImageButton btnRegresar;
    private PlantasAdapter adapter;
    private appLogger appLogger; // logger

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_dispositivos);

        // Inicializar appLogger con UID o "anonimo"
        String uid = "anonimo";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) uid = firebaseUser.getUid();
        appLogger = new appLogger(uid);

        // Log: abrir pantalla
        appLogger.logEvent("abrirPantalla", "Usuario abrió dispositivos_activity");

        recyclerPlantas = findViewById(R.id.recyclerPlantas);
        txtCantidadPlantas = findViewById(R.id.txtCantidadPlantas);

        listaPlantas = new ArrayList<>();
        adapter = new PlantasAdapter(listaPlantas);
        recyclerPlantas.setLayoutManager(new LinearLayoutManager(this));
        recyclerPlantas.setAdapter(adapter);

        // Para GPS
        txtGPS = findViewById(R.id.txtGPS);
        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });

        //-----------------------------------------------
        cargarPlantasUsuario();

        btnRegresar = findViewById(R.id.btn_dispositivos_regresar);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appLogger.logEvent("clickBoton", "Usuario presionó btnRegresar");
                finish(); // Cierra la actividad actual y vuelve atrás
            }
        });
    }

    private void cargarPlantasUsuario() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        String email = firebaseUser.getEmail();
        DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");

        refUsuarios.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listaPlantas.clear();
                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            DataSnapshot plantasSnap = userSnap.child("plantas");
                            for (DataSnapshot plantaSnap : plantasSnap.getChildren()) {
                                Planta planta = plantaSnap.getValue(Planta.class);
                                planta.setId(plantaSnap.getKey());
                                listaPlantas.add(planta);
                            }
                        }

                        adapter.notifyDataSetChanged();
                        txtCantidadPlantas.setText("Tienes " + listaPlantas.size() + " plantas vinculadas");

                        // Log: plantas cargadas
                        appLogger.logEvent("cargarPlantas", "Usuario cargó " + listaPlantas.size() + " plantas");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(dispositivos_activity.this,
                                "Error al cargar plantas", Toast.LENGTH_SHORT).show();
                        appLogger.logEvent("error", "Error al cargar plantas: " + error.getMessage());
                    }
                });
    }
}
