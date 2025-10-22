package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.happyplant.R;
import com.example.happyplant.utils.GPSHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.example.happyplant.repository.UsuarioRepository;


import java.util.HashMap;
import java.util.Map;

public class ecoUsuario_activity extends AppCompatActivity {

    //+-------------------------------------------------------------------------------------------+
    // para GPS
    private TextView txtGPS;
    private GPSHelper gpsHelper;
    private ImageButton btnUsuario_regresar;
    private TextInputEditText editNombre, editPassword;
    private TextInputEditText editPasswordActual;
    private TextView txtEmail;
    private Button btnGuardarCambios;
    private FirebaseAuth auth;
    private DatabaseReference refUsuarios;
    private UsuarioRepository usuarioRepository;

    //+-------------------------------------------------------------------------------------------+
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_usuario);

        usuarioRepository = new UsuarioRepository();

        btnUsuario_regresar = findViewById(R.id.btn_ecoUsuario_regresar);
        editNombre = findViewById(R.id.editNombre);
        editPassword = findViewById(R.id.editPassword);
        txtEmail = findViewById(R.id.txtEmail);
        btnGuardarCambios = findViewById(R.id.btn_ecoUsuario_GuardarCambios);
        editPasswordActual = findViewById(R.id.edit_ecoUsuario_PasswordActual);

        auth = FirebaseAuth.getInstance();
        refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");

        cargarDatosUsuario();

        btnGuardarCambios.setOnClickListener(v -> guardarCambios());

        btnUsuario_regresar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(ecoUsuario_activity.this, perfil_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });
        //Para GPS
        txtGPS = findViewById(R.id.txtGPS);

        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });
    }

    //+-------------------------------------------------------------------------------------------+

    private void cargarDatosUsuario() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        String email = firebaseUser.getEmail();

        refUsuarios.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                String nombre = userSnap.child("nombre").getValue(String.class);
                                String password = userSnap.child("passwordHash").getValue(String.class);

                                editNombre.setText(nombre);
                                txtEmail.setText(email);
                                editPassword.setText(password);

                                String uid = userSnap.getKey(); // CAMBIO: UID guardado si se necesita
                                Log.d("EcoUsuario", "Usuario cargado: " + nombre + " (UID: " + uid + ")");
                            }
                        } else {
                            Toast.makeText(ecoUsuario_activity.this,
                                    "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ecoUsuario_activity.this,
                                "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show();
                        Log.e("EcoUsuario", "Error Firebase", error.toException());
                    }
                });
    }

    //+-------------------------------------------------------------------------------------------+
    private void guardarCambios() {
        String nuevoNombre = editNombre.getText().toString().trim();
        String nuevaPassword = editPassword.getText().toString().trim();
        String passwordActual = editPasswordActual.getText().toString().trim();

        if (nuevoNombre.isEmpty() || passwordActual.isEmpty() || nuevaPassword.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        guardarNombre(nuevoNombre);
        guardarPassword(passwordActual, nuevaPassword);
    }


    private void guardarNombre(String nuevoNombre) {
        String uid = auth.getCurrentUser().getUid();
        usuarioRepository.actualizarNombre(uid, nuevoNombre, new UsuarioRepository.RegistroCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ecoUsuario_activity.this, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ecoUsuario_activity.this, "Error al actualizar nombre: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarPasswordDecorativa(String uid) {
        Map<String, Object> actualizacion = new HashMap<>();
        actualizacion.put("passwordHash", "**********");
        // solo decorativo
        refUsuarios.child(uid).updateChildren(actualizacion)
                .addOnSuccessListener(aVoid -> {
                    // toast informativo
                    Toast.makeText(this, "Indicativo de contraseña actualizado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar indicador de contraseña: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarPassword(String passwordActual, String nuevaPassword) {
        usuarioRepository.actualizarPassword(passwordActual, nuevaPassword, new UsuarioRepository.RegistroCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ecoUsuario_activity.this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();

                String uid = auth.getCurrentUser().getUid();
                actualizarPasswordDecorativa(uid);

                limpiarCamposContrasena();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ecoUsuario_activity.this, "Error al actualizar contraseña: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume () {
        super.onResume();
        limpiarCamposContrasena();
    }

    private void limpiarCamposContrasena () {
        editPasswordActual.setText("");
        editPassword.setText("");
    }


}
