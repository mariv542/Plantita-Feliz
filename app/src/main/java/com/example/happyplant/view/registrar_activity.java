package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.utils.GPSHelper;
import com.example.happyplant.utils.appLogger;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.example.happyplant.repository.UsuarioRepository;

public class registrar_activity extends AppCompatActivity {

    private TextInputEditText etEmail, etPass, etConfirmPass;
    private UsuarioRepository usuarioRepo;
    private Button btnRegister;
    private TextView txtLogin, txtGPS;
    private FirebaseAuth auth;
    private GPSHelper gpsHelper;
    private ImageButton btnRegistrar_regresar;
    private appLogger appLogger; // logger

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_usuario);

        // Inicializar appLogger con UID o "anonimo"
        String uid = "anonimo";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        appLogger = new appLogger(uid);

        // Log: abrir pantalla
        appLogger.logEvent("abrirPantalla", "Usuario abrió registrar_activity");

        usuarioRepo = new UsuarioRepository();

        // Botón para regresar a Login
        btnRegistrar_regresar = findViewById(R.id.btn_registrar_regresar);

        // Para GPS
        txtGPS = findViewById(R.id.txtGPS);
        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });

        // Campos de registro
        etEmail = findViewById(R.id.etEmail);
        etPass  = findViewById(R.id.etPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        auth = FirebaseAuth.getInstance();

        // Click registrar
        btnRegister.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnRegister");
            registrar();
        });

        // Click login
        txtLogin.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó txtLogin");
            startActivity(new Intent(registrar_activity.this, login_activity.class));
            finish();
        });

        // Click regresar
        btnRegistrar_regresar.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnRegistrar_regresar");
            Intent intent = new Intent(registrar_activity.this, login_activity.class);
            startActivity(intent);
        });
    }

    private void registrar() {
        String email = etEmail.getText().toString().trim();
        String pass  = etPass.getText().toString().trim();
        String confirmPass = etConfirmPass.getText().toString().trim();

        if (!validar(email, pass, confirmPass)) return;

        String nombreDefault = "Usuario";

        usuarioRepo.registrarUsuarioConAuth(nombreDefault, email, pass,
                new UsuarioRepository.RegistroCallback() {
                    @Override
                    public void onSuccess() {
                        appLogger.logEvent("registroExitoso", "Usuario registrado correctamente: " + email);
                        Toast.makeText(registrar_activity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(registrar_activity.this, login_activity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        appLogger.logEvent("registroFallido", "Error al registrar usuario: " + error);
                        Toast.makeText(registrar_activity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validar(String email, String pass, String confirmPass) {
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
        if (!pass.equals(confirmPass)) {
            etConfirmPass.setError("Las contraseñas no coinciden");
            etConfirmPass.requestFocus();
            return false;
        }
        return true;
    }
}
