package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.utils.GPSHelper;
import com.google.android.material.card.MaterialCardView;

public class perfil_activity extends AppCompatActivity {

    private MaterialCardView card_InformacionPersonal;
    private ImageButton btnPerfil_regresar;
    private TextView txtGPS;
    private GPSHelper gpsHelper;
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

        //+--------------------------------------------------------------------------------------------+

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
