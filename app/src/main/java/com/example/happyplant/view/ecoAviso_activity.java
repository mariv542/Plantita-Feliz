package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.utils.GPSHelper;

public class ecoAviso_activity extends AppCompatActivity {

    private TextView txtGPS;
    private GPSHelper gpsHelper;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.eco_aviso);
        //+--------------------------------------------------------------------------------------------+
        txtGPS = findViewById(R.id.txtGPS);

        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });
        ImageButton btnEcoAviso_regresar = findViewById(R.id.btn_ecoAviso_regresar);
        //+--------------------------------------------------------------------------------------------+

        btnEcoAviso_regresar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(ecoAviso_activity.this, menu_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });
    }
}
