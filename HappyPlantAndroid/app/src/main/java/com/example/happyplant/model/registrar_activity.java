package com.example.happyplant.model;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;

public class registrar_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.registrar_usuario);
        //+--------------------------------------------------------------------------------------------+

        ImageButton btnRegistrar_regresar = findViewById(R.id.btn_registrar_regresar);
        //+--------------------------------------------------------------------------------------------+

        btnRegistrar_regresar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(registrar_activity.this, login_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });
    }
}