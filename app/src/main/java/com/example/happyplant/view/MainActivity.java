package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.utils.appLogger;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private appLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inicializar logger con el UID del usuario si está logueado, si no usar "anonimo"
        String uid = "anonimo";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        logger = new appLogger(uid);

        // Log: abrir MainActivity
        logger.logEvent("AbrirPantalla", "Usuario abrió MainActivity");

        Button btnMain_comenzar = findViewById(R.id.btn_main_comenzar);

        btnMain_comenzar.setOnClickListener(v -> {
            // Log: click en el botón
            logger.logEvent("ClickBoton", "Presionó btn_main_comenzar");

            Intent intent = new Intent(MainActivity.this, login_activity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Log: pantalla visible
        logger.logEvent("PantallaVisible", "Usuario está en MainActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Log: pantalla oculta
        logger.logEvent("PantallaOculta", "Usuario salió de MainActivity");
    }
}
