package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;

public class ecoControl_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.eco_control);
        //+--------------------------------------------------------------------------------------------+

        ImageButton btnEcoControl_regresar = findViewById(R.id.btn_ecoControl_regresar);
        //+--------------------------------------------------------------------------------------------+

        btnEcoControl_regresar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(ecoControl_activity.this, menu_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });
    }
}
