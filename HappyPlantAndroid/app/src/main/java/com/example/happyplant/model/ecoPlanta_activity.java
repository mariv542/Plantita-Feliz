package com.example.happyplant.model;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;

public class ecoPlanta_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.eco_planta);
        //+--------------------------------------------------------------------------------------------+

        ImageButton btnEcoPlanta_regresar = findViewById(R.id.btn_ecoPlanta_regresar);
        //+--------------------------------------------------------------------------------------------+

        btnEcoPlanta_regresar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(ecoPlanta_activity.this, menu_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });
    }
}
