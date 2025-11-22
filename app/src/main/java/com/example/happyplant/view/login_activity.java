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
import com.example.happyplant.utils.appLogger;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class login_activity extends AppCompatActivity {

    private TextInputEditText etEmail, etPass;
    private Button btnLogin_iniciar;
    private TextView txtLogin_registrar, txtGPS;
    private FirebaseAuth auth;
    private GPSHelper gpsHelper;
    private appLogger appLogger; // clase appLogger con variable appLogger

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.login);

        // Inicializar appLogger con UID o "anonimo"
        String uid = "anonimo";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        appLogger = new appLogger(uid);

        // Log: abrir pantalla
        appLogger.logEvent("abrirPantalla", "Usuario abrió login_activity");

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
        etEmail = findViewById(R.id.inputCorreo).findViewById(R.id.etEmail);
        etPass  = findViewById(R.id.inputPassword).findViewById(R.id.etPass);
        btnLogin_iniciar = findViewById(R.id.btn_Login_iniciar);
        txtLogin_registrar = findViewById(R.id.txt_login_registrar);

        // Log y acción: click iniciar sesión
        btnLogin_iniciar.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btn_Login_iniciar");
            iniciarSesion();
        });

        // Log y acción: click registrar
        txtLogin_registrar.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó txt_login_registrar");
            Intent intent = new Intent(login_activity.this, registrar_activity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            appLogger.logEvent("loginExitoso", "Usuario ya logueado");
            irAMenu();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appLogger.logEvent("pantallaVisible", "Usuario está en login_activity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        appLogger.logEvent("pantallaOculta", "Usuario salió de login_activity");
    }

    private void iniciarSesion() {
        String email = etEmail.getText().toString().trim();
        String pass  = etPass.getText().toString().trim();

        if (!validar(email, pass)) return;

        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appLogger.logEvent("loginExitoso", "Usuario inició sesión correctamente");
                        Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();

                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            registrarTokenFCM(user.getUid());
                        }

                        irAMenu();
                    } else {
                        appLogger.logEvent("loginFallido", "Error: Usuario no registrado o contraseña incorrecta");
                        Toast.makeText(this, "Error: Usuario no registrado", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void registrarTokenFCM(String uid) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(tokenTask -> {
                    if (tokenTask.isSuccessful()) {
                        String token = tokenTask.getResult();
                        FirebaseDatabase.getInstance().getReference("usuarios")
                                .child(uid)
                                .child("token")
                                .setValue(token);
                        Toast.makeText(this, "Token registrado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al obtener token FCM", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        gpsHelper.onRequestPermissionsResult(requestCode, grantResults, (lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });
    }
}
