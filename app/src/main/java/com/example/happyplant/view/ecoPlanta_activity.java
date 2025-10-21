package com.example.happyplant.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.model.Parametros;
import com.example.happyplant.model.Planta;
import com.example.happyplant.model.Usuario;
import com.example.happyplant.repository.PlantaRepository;
import com.example.happyplant.repository.UsuarioRepository;
import com.example.happyplant.utils.GPSHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ecoPlanta_activity extends AppCompatActivity {

    private EditText etEcoPlanta_nombrePlanta, etEcoPlanta_idMaceta;
    private Button btnEcoPlanta_guardar;
    private UsuarioRepository usuarioRepo;
    private PlantaRepository plantaRepo;
    private Usuario usuarioLogueado;
    private FirebaseAuth auth;
    private TextView txtGPS;
    private ImageButton btnEcoPlanta_regresar;
    private GPSHelper gpsHelper;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.eco_planta);
        //+--------------------------------------------------------------------------------------------+

        //agregar planta
        etEcoPlanta_nombrePlanta = findViewById(R.id.et_ecoPlanta_nombrePlanta);
        etEcoPlanta_idMaceta = findViewById(R.id.et_ecoPlanta_idMaceta);
        btnEcoPlanta_guardar = findViewById(R.id.btn_ecoPlanta_guardar);

        // firebase
        auth = FirebaseAuth.getInstance();
        usuarioRepo = new UsuarioRepository();

        cargarUsuarioLogueado();
        // gps
        txtGPS = findViewById(R.id.txtGPS);

        gpsHelper = new GPSHelper(this);

        btnEcoPlanta_regresar = findViewById(R.id.btn_ecoPlanta_regresar);


        //+--------------------------------------------------------------------------------------------+


        btnEcoPlanta_guardar.setOnClickListener(v -> guardarPlanta());

        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });

        btnEcoPlanta_regresar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(ecoPlanta_activity.this, menu_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });
    }

    //+--------------------------------------------------------------------------------------------+

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

                                // inicializamos plantaRepo con el ID correcto del nodo usuario
                                plantaRepo = new PlantaRepository(usuarioLogueado.getId());
                                Toast.makeText(ecoPlanta_activity.this, "Usuario cargado correctamente.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ecoPlanta_activity.this, "Usuario no encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("EcoPlanta", "Error al consultar usuario", error.toException());
                    }
                });
    }
    // 1 Valida campos de entrada
    private boolean validarCampos() {
        String idPlanta = etEcoPlanta_idMaceta.getText().toString().trim();
        String nombrePersonalizado = etEcoPlanta_nombrePlanta.getText().toString().trim();
        boolean valido = true;

        if (idPlanta.isEmpty()) {
            etEcoPlanta_idMaceta.setError("ID requerido");
            valido = false;
        }
        if (nombrePersonalizado.isEmpty()) {
            etEcoPlanta_nombrePlanta.setError("Nombre requerido");
            valido = false;
        }

        return valido;
    }

    // 2 Obtener ID del usuario autenticado
    private String obtenerIdUsuario() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "Error: usuario no autenticado.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    private void guardarPlanta() {
        if (!validarCampos()) return;

        String idPlanta = etEcoPlanta_idMaceta.getText().toString().trim();
        String idUsuario = obtenerIdUsuario();
        if (idUsuario == null) return;

        // Obtener planta desde precarga global
        FirebaseDatabase.getInstance().getReference("plantas").child(idPlanta)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Firebase convierte automáticamente todo a Planta
                            Planta planta = snapshot.getValue(Planta.class);
                            if (planta == null) {
                                Toast.makeText(ecoPlanta_activity.this, "Error al leer la planta.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Cambiar nombre al personalizado
                            planta.setNombre(etEcoPlanta_nombrePlanta.getText().toString().trim());

                            // Guardar planta en /usuarios/{userId}/plantas
                            plantaRepo.guardarPlanta(planta);

                            Toast.makeText(ecoPlanta_activity.this, "Planta agregada correctamente.", Toast.LENGTH_SHORT).show();
                            etEcoPlanta_nombrePlanta.setText("");
                            etEcoPlanta_idMaceta.setText("");
                            etEcoPlanta_idMaceta.setBackgroundTintList(getColorStateList(R.color.teal_200));

                        } else {
                            // ID no existe → marcar rojo
                            etEcoPlanta_idMaceta.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            Toast.makeText(ecoPlanta_activity.this, "No existe una planta con ese ID.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ecoPlanta_activity.this, "Error al obtener planta: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}