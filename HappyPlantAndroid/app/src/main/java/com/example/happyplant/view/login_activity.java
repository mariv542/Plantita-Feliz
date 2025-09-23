package com.example.happyplant.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;

public class login_activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.login);
        //+--------------------------------------------------------------------------------------------+

        Button btnLogin_iniciar = findViewById(R.id.btn_Login_iniciar);
        TextView txtLogin_registrar = findViewById(R.id.txt_login_registrar);
        //+--------------------------------------------------------------------------------------------+

        btnLogin_iniciar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(login_activity.this, menu_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            finish();
        });

        txtLogin_registrar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(login_activity.this, registrar_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            finish();
        });
    }

}
