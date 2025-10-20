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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.example.happyplant.model.Usuario;
import com.example.happyplant.repository.UsuarioRepository;


public class registrar_activity extends AppCompatActivity {

    private TextInputEditText etEmail, etPass, etConfirmPass;
    private UsuarioRepository usuarioRepo;
    private Button btnRegister;
    private TextView txtLogin, txtGPS;
    private FirebaseAuth auth;
    private GPSHelper gpsHelper;
    ImageButton btnRegistrar_regresar;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.registrar_usuario);

        usuarioRepo = new UsuarioRepository();
        //+--------------------------------------------------------------------------------------------+
        //Boton para regresar a Login
        btnRegistrar_regresar = findViewById(R.id.btn_registrar_regresar);

        //Para GPS
        txtGPS = findViewById(R.id.txtGPS);
        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });

        //Campos de registros
        etEmail = findViewById(R.id.etEmail);
        etPass  = findViewById(R.id.etPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        auth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(v -> registrar());

        txtLogin.setOnClickListener(v -> {
            startActivity(new Intent(registrar_activity.this, login_activity.class));
            finish();
        });

        //Para poder regresar a Login
        btnRegistrar_regresar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(registrar_activity.this, login_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });
    }

    private void registrar() {
        String email = etEmail.getText().toString().trim();
        String pass  = etPass.getText().toString().trim();
        String confirmPass = etConfirmPass.getText().toString().trim();

        if (!validar(email, pass, confirmPass)) return;

        // Nombre por defecto
        String nombreDefault = "Usuario";

        // Llamada al repository con callback
        usuarioRepo.registrarUsuarioConAuth(nombreDefault, email, pass,
                new UsuarioRepository.RegistroCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(registrar_activity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(registrar_activity.this, login_activity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
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