package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.utils.GPSHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login_activity extends AppCompatActivity {

    private TextInputEditText etEmail, etPass;
    private Button btnLogin_iniciar;
    private TextView txtLogin_registrar, txtGPS;
    private FirebaseAuth auth;
    private GPSHelper gpsHelper;


    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.login);
        //GPS
        txtGPS = findViewById(R.id.txtGPS);

        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });

        //Firebase
        auth = FirebaseAuth.getInstance();

        //Campos de login
        etEmail = findViewById(R.id.inputCorreo).findViewById(R.id.etEmail); // si tienes TextInputEditText dentro
        etPass  = findViewById(R.id.inputPassword).findViewById(R.id.etPass);
        btnLogin_iniciar = findViewById(R.id.btn_Login_iniciar);
        txtLogin_registrar = findViewById(R.id.txt_login_registrar);

        btnLogin_iniciar.setOnClickListener(v -> iniciarSesion());

        txtLogin_registrar.setOnClickListener(v -> {
            Intent intent = new Intent(login_activity.this, registrar_activity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            irAMenu();
        }
    }

    private void iniciarSesion() {
        String email = etEmail.getText().toString().trim();
        String pass  = etPass.getText().toString().trim();

        if (!validar(email, pass)) return;

        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
                        irAMenu();
                    } else {
                        Toast.makeText(this, "Error: Usuario no registrado", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validar(String email, String pass) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Requerido");
            etEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(pass)) {
            etPass.setError("Requerido");
            etPass.requestFocus();
            return false;
        }
        if (pass.length() < 6) {
            etPass.setError("Mínimo 6 caracteres");
            etPass.requestFocus();
            return false;
        }
        return true;
    }

    private void irAMenu() {
        startActivity(new Intent(this, menu_activity.class));
        finish();
    }
    // Cambiar a MainActivitytodo cuando este listo el avance 2
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        gpsHelper.onRequestPermissionsResult(requestCode, grantResults, (lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });
    }

}
