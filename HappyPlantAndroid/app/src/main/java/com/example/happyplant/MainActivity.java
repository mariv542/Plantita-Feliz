package com.example.happyplant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.happyplant.model.login_activity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        Button btnMain_comenzar = findViewById(R.id.btn_main_comenzar);

        btnMain_comenzar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, login_activity.class);
            startActivity(intent);
        });

    }
}