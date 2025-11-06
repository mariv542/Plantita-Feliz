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
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class perfil_activity extends AppCompatActivity {

    private FirebaseAuth auth;
    private MaterialCardView card_InformacionPersonal, card_dispositivos, cardSoporte, cardNotificaciones;
    private MaterialCardView card_logout;
    private ImageButton btnPerfil_regresar;
    private TextView txtGPS;
    private GPSHelper gpsHelper;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.eco_perfil);
        //+--------------------------------------------------------------------------------------------+

        //firebase
        auth = FirebaseAuth.getInstance();

        btnPerfil_regresar = findViewById(R.id.btn_perfil_regresar);
        //Para GPS
        txtGPS = findViewById(R.id.txtGPS);

        // card informacion personal
        card_InformacionPersonal = findViewById(R.id.card_perfil_informacionPersonal);
        card_dispositivos = findViewById(R.id.cardDispositivos);
        cardSoporte = findViewById(R.id.cardSoporte);
        cardNotificaciones = findViewById(R.id.cardNotificaciones);

        // card log out
        card_logout = findViewById(R.id.card_perfil_Salir);

        gpsHelper = new GPSHelper(this);
        //+--------------------------------------------------------------------------------------------+

        card_logout.setOnClickListener(v -> mostrarDialogoCerrarSesion());

        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });

        card_InformacionPersonal.setOnClickListener(v -> {
            Intent intent = new Intent(perfil_activity.this, ecoUsuario_activity.class);
            startActivity(intent);
        });

        card_dispositivos.setOnClickListener(v -> {
            Intent intent = new Intent(perfil_activity.this, dispositivos_activity.class);
            startActivity(intent);
        });

        btnPerfil_regresar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(perfil_activity.this, menu_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });

        cardSoporte.setOnClickListener(v -> {
            Intent intent = new Intent(perfil_activity.this, soporteActivity.class);
            startActivity(intent);
        });
        //+--------------------------------------------------------------------------------------------+
        cardNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Desde Android 8 en adelante: va directo a la pantalla de notificaciones de la app
                intent.setAction(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, getPackageName());
            } else {
                // Compatibilidad con versiones anteriores
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(android.net.Uri.parse("package:" + getPackageName()));
            }

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No se pudo abrir la configuración de notificaciones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoCerrarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar sesión");
        builder.setMessage("¿Estás seguro que deseas cerrar sesión?");
        builder.setPositiveButton("Sí", (dialog, which) -> {
            Toast.makeText(this, "Cerrando sesión en 3 segundos...", Toast.LENGTH_SHORT).show();

            // Esperar 3 segundos antes de cerrar sesión
            new Handler().postDelayed(() -> {
                auth.signOut();
                // cerrar sesión Firebase

                Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();

                // Ir a MainActivity
                Intent intent = new Intent(perfil_activity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }, 3000);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}



