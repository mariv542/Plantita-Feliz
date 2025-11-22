package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.utils.GPSHelper;
import com.example.happyplant.utils.appLogger;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class perfil_activity extends AppCompatActivity {

    private FirebaseAuth auth;
    private MaterialCardView card_InformacionPersonal, card_dispositivos, cardSoporte, cardNotificaciones;
    private MaterialCardView card_logout;
    private ImageButton btnPerfil_regresar;
    private TextView txtGPS;
    private GPSHelper gpsHelper;
    private appLogger appLogger;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.eco_perfil);

        // Inicializar Firebase y AppLogger
        auth = FirebaseAuth.getInstance();
        String uid = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : "anonimo";
        appLogger = new appLogger(uid);
        appLogger.logEvent("abrirPantalla", "Usuario abrió perfil_activity");

        // Inicializar vistas
        txtGPS = findViewById(R.id.txtGPS);
        btnPerfil_regresar = findViewById(R.id.btn_perfil_regresar);
        card_InformacionPersonal = findViewById(R.id.card_perfil_informacionPersonal);
        card_dispositivos = findViewById(R.id.cardDispositivos);
        cardSoporte = findViewById(R.id.cardSoporte);
        cardNotificaciones = findViewById(R.id.cardNotificaciones);
        card_logout = findViewById(R.id.card_perfil_Salir);

        // GPS
        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
            appLogger.logEvent("gpsObtenido", "Ciudad detectada: " + ciudad);
        });

        // Click listeners
        card_InformacionPersonal.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó card_InformacionPersonal");
            startActivity(new Intent(perfil_activity.this, ecoUsuario_activity.class));
        });

        card_dispositivos.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó card_dispositivos");
            startActivity(new Intent(perfil_activity.this, dispositivos_activity.class));
        });

        cardSoporte.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó cardSoporte");
            startActivity(new Intent(perfil_activity.this, soporteActivity.class));
        });

        cardNotificaciones.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó cardNotificaciones");
            abrirConfiguracionNotificaciones();
        });

        btnPerfil_regresar.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnPerfil_regresar");
            startActivity(new Intent(perfil_activity.this, menu_activity.class));
        });

        card_logout.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó card_logout");
            mostrarDialogoCerrarSesion();
        });
    }

    private void abrirConfiguracionNotificaciones() {
        Intent intent = new Intent();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            intent.setAction(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else {
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(android.net.Uri.parse("package:" + getPackageName()));
        }

        try {
            startActivity(intent);
            appLogger.logEvent("notificaciones", "Se abrió la configuración de notificaciones");
        } catch (Exception e) {
            appLogger.logEvent("errorNotificaciones", "Error al abrir configuración de notificaciones: " + e.getMessage());
            Toast.makeText(this, "No se pudo abrir la configuración de notificaciones", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoCerrarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar sesión");
        builder.setMessage("¿Estás seguro que deseas cerrar sesión?");
        builder.setPositiveButton("Sí", (dialog, which) -> {
            appLogger.logEvent("cerrarSesion", "Usuario confirmó cerrar sesión");
            Toast.makeText(this, "Cerrando sesión en 3 segundos...", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> {
                auth.signOut();
                appLogger.logEvent("cerrarSesion", "Sesión cerrada correctamente");

                Intent intent = new Intent(perfil_activity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }, 3000);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            appLogger.logEvent("cerrarSesion", "Usuario canceló cerrar sesión");
            dialog.dismiss();
        });

        builder.show();
    }
}
