package com.example.happyplant.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.happyplant.R;
import com.example.happyplant.utils.GPSHelper;
import com.google.android.material.card.MaterialCardView;

public class perfil_activity extends AppCompatActivity {

    private MaterialCardView card_InformacionPersonal;
    private ImageButton btnPerfil_regresar;
    private TextView txtGPS;
    private GPSHelper gpsHelper;
    private Switch switchModoNegro;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.eco_perfil);
        //+--------------------------------------------------------------------------------------------+

        btnPerfil_regresar = findViewById(R.id.btn_perfil_regresar);
        //Para GPS
        txtGPS = findViewById(R.id.txtGPS);

        // card informacion personal
        card_InformacionPersonal = findViewById(R.id.card_perfil_informacionPersonal);

        // 1 inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);

        // 2 Aplicar modo guardado
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        // 3 Referencia al Switch
        switchModoNegro = findViewById(R.id.switchModoNegro);
        switchModoNegro.setChecked(isDarkMode);
        //+--------------------------------------------------------------------------------------------+

        // 4. Escuchar cambios del Switch
        switchModoNegro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Guardar preferencia
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            // Cambiar modo oscuro/claro
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            // Opcional reiniciar la actividad para aplicar cambios inmediatos
            recreate();
        });

        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });

        card_InformacionPersonal.setOnClickListener(v -> {
            Intent intent = new Intent(perfil_activity.this, ecoUsuario_activity.class);
            startActivity(intent);
        });

        btnPerfil_regresar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(perfil_activity.this, menu_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });
        //+--------------------------------------------------------------------------------------------+
    }
}
