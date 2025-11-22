package com.example.happyplant.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.utils.appLogger;
import com.google.firebase.auth.FirebaseAuth;

public class soporteActivity extends AppCompatActivity {

    private ImageButton btnRegresar;
    private TextView tvCorreoSoporte;
    private appLogger appLogger;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        auth = FirebaseAuth.getInstance();
        String uid = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : "anonimo";
        appLogger = new appLogger(uid);
        appLogger.logEvent("abrirPantalla", "Usuario abrió soporteActivity");

        btnRegresar = findViewById(R.id.btn_soporte_regresar);
        tvCorreoSoporte = findViewById(R.id.tvCorreoSoporte);

        // Regresar a la pantalla anterior
        btnRegresar.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnRegresar");
            finish();
        });

        // Abrir correo al hacer click
        tvCorreoSoporte.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó tvCorreoSoporte");
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:HappyPlanta@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Soporte HappyPlant");
            try {
                startActivity(intent);
                appLogger.logEvent("soporteCorreo", "Se abrió la app de correo para soporte");
            } catch (Exception e) {
                appLogger.logEvent("errorSoporteCorreo", "Error al abrir app de correo: " + e.getMessage());
            }
        });
    }
}
