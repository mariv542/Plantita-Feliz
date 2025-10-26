package com.example.happyplant.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;

public class soporteActivity extends AppCompatActivity {

    private ImageButton btnRegresar;
    private TextView tvCorreoSoporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        btnRegresar = findViewById(R.id.btn_soporte_regresar);
        tvCorreoSoporte = findViewById(R.id.tvCorreoSoporte);

        // Regresar a la pantalla anterior
        btnRegresar.setOnClickListener(v -> finish());

        // Abrir correo al hacer click
        tvCorreoSoporte.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:HappyPlanta@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Soporte HappyPlant");
            startActivity(intent);
        });
    }
}
