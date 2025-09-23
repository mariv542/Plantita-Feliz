package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;

public class menu_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.menu);
        //+--------------------------------------------------------------------------------------------+

        ImageButton btnMenu_ecoAvance = findViewById(R.id.btn_menu_ecoAvance);
        ImageButton btnMenu_ecoAviso = findViewById(R.id.btn_menu_ecoAviso);
        ImageButton btnMenu_ecoPlanta = findViewById(R.id.btn_menu_ecoPlanta);
        ImageButton btnMenu_ecoControl = findViewById(R.id.btn_menu_ecoControl);
        ImageButton btnMenu_perfil = findViewById(R.id.btn_menu_perfil);
        //+--------------------------------------------------------------------------------------------+

        btnMenu_ecoAvance.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(menu_activity.this, ecoAvance_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });

        btnMenu_ecoAviso.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(menu_activity.this, ecoAviso_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });

        btnMenu_ecoPlanta.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(menu_activity.this, ecoPlanta_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });

        btnMenu_ecoControl.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(menu_activity.this, ecoControl_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });

        btnMenu_perfil.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(menu_activity.this, perfil_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });
    }
}
