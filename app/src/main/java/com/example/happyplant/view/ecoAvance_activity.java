package com.example.happyplant.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.model.HumedadAmbiental;
import com.example.happyplant.model.HumedadSuelo;
import com.example.happyplant.model.Parametros;
import com.example.happyplant.model.Planta;
import com.example.happyplant.model.Rango;
import com.example.happyplant.model.Temperatura;
import com.example.happyplant.utils.GPSHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class ecoAvance_activity extends AppCompatActivity {
    // para GPS
    private TextView txtGPS;
    private GPSHelper gpsHelper;
    private Spinner spinnerPlantas;
    private ImageView chartView;
    private TextView txtNombrePlanta, txtTemperatura, txtHumedadSuelo, txtHumedadAmbiental;
    private ImageButton btnEcoAvanceRegresar;

    private List<Planta> listaPlantas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.eco_avance);
        //+--------------------------------------------------------------------------------------------+
            //inicializar vistas
        btnEcoAvanceRegresar = findViewById(R.id.btn_ecoAvance_regresar);
        spinnerPlantas = findViewById(R.id.spinnerPlantas);
        chartView = findViewById(R.id.chartView);
        txtNombrePlanta = findViewById(R.id.txtNombrePlanta);
        txtTemperatura = findViewById(R.id.txtTemperatura);
        txtHumedadSuelo = findViewById(R.id.txtHumedadSuelo);
        txtHumedadAmbiental = findViewById(R.id.txtHumedadAmbiental);

        //Para GPS
        txtGPS = findViewById(R.id.txtGPS);

        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });

        //+--------------------------------------------------------------------------------------------+

        btnEcoAvanceRegresar.setOnClickListener(v -> {
            // Creamos un Intent para ir a menu_activity
            Intent intent = new Intent(ecoAvance_activity.this, menu_activity.class);
            startActivity(intent);
            // para serrar la pestaña dde login y que no vuelva atras dar finish:
            // finish();
        });

        //cargar lista de plantas
        //cargarPlantasSimuladas();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, obtenerNombresPLantas(listaPlantas));

        spinnerPlantas.setAdapter(adapter);

        spinnerPlantas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Planta planta = listaPlantas.get(position);
                actualizarDashboard(planta);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    //metodos

    private List<String> obtenerNombresPLantas(List<Planta> plantas){
        List<String> nombre = new ArrayList<>();
        for (Planta p : plantas){
            nombre.add(p.getNombre());
        }
        return nombre;
    }
//    private void cargarPlantasSimuladas(){
//        //planta 1
//        Planta cactus = new Planta();
//        cactus.setId("1");
//        cactus.setNombre("Cactus");
//
//        Parametros p1 = new Parametros();
//        p1.setRangoHumedadSuelo(new Rango("r1", 10, 40));
//        p1.setRangoTemperatura(new Rango("r2", 20, 30));
//        p1.setRangoHumedadAmbiental(new Rango("r3", 30, 50));
//        p1.setRangoNivelAgua(new Rango("r4", 0, 5));
//        cactus.setParametros(p1);
//
//        // Temperaturas
//        List<Temperatura> temps1 = new ArrayList<>();
//        temps1.add(new Temperatura("1", 28, LocalDateTime.now().minusHours(2)));
//        temps1.add(new Temperatura("1", 30, LocalDateTime.now()));
//        cactus.setTemperaturas(temps1);
//
//        // Humedad de suelo
//        List<HumedadSuelo> humS1 = new ArrayList<>();
//        humS1.add(new HumedadSuelo("1", 35, LocalDateTime.now().minusHours(2)));
//        humS1.add(new HumedadSuelo("1", 32, LocalDateTime.now()));
//        cactus.setHumedadesSuelo(humS1);
//
//        // Humedad ambiental
//        List<HumedadAmbiental> humA1 = new ArrayList<>();
//        humA1.add(new HumedadAmbiental("1", 40, LocalDateTime.now().minusHours(2)));
//        humA1.add(new HumedadAmbiental("1", 45, LocalDateTime.now()));
//        cactus.setHumedadesAmbientales(humA1);
//
//        listaPlantas.add(cactus);
//
//        // Planta 2
//        Planta helecho = new Planta();
//        helecho.setId("2");
//        helecho.setNombre("Helecho");
//
//        Parametros p2 = new Parametros();
//        p2.setRangoHumedadSuelo(new Rango("r5", 40, 70));
//        p2.setRangoTemperatura(new Rango("r6", 18, 25));
//        p2.setRangoHumedadAmbiental(new Rango("r7", 60, 80));
//        p2.setRangoNivelAgua(new Rango("r8", 1, 6));
//        helecho.setParametros(p2);
//
//        List<Temperatura> temps2 = new ArrayList<>();
//        temps2.add(new Temperatura("2", 22, LocalDateTime.now()));
//        helecho.setTemperaturas(temps2);
//
//        List<HumedadSuelo> humS2 = new ArrayList<>();
//        humS2.add(new HumedadSuelo("2", 65, LocalDateTime.now()));
//        helecho.setHumedadesSuelo(humS2);
//
//        List<HumedadAmbiental> humA2 = new ArrayList<>();
//        humA2.add(new HumedadAmbiental("2", 70, LocalDateTime.now()));
//        helecho.setHumedadesAmbientales(humA2);
//
//        listaPlantas.add(helecho);
//
//        // Planta 3
//        Planta carnivora = new Planta();
//        carnivora.setId("3");
//        carnivora.setNombre("Carnívora");
//
//        Parametros p3 = new Parametros();
//        p3.setRangoHumedadSuelo(new Rango("r9", 25, 47));
//        p3.setRangoTemperatura(new Rango("r10", 36, 50));
//        p3.setRangoHumedadAmbiental(new Rango("r11", 34, 56));
//        p3.setRangoNivelAgua(new Rango("r12", 3, 10));
//        carnivora.setParametros(p3);
//
//        List<Temperatura> temps3 = new ArrayList<>();
//        temps3.add(new Temperatura("3", 50, LocalDateTime.now().minusHours(2)));
//        temps3.add(new Temperatura("3", 40, LocalDateTime.now()));
//        carnivora.setTemperaturas(temps3);
//
//        List<HumedadSuelo> humS3 = new ArrayList<>();
//        humS3.add(new HumedadSuelo("3", 30, LocalDateTime.now().minusHours(2)));
//        humS3.add(new HumedadSuelo("3", 27, LocalDateTime.now()));
//        carnivora.setHumedadesSuelo(humS3);
//
//        List<HumedadAmbiental> humA3 = new ArrayList<>();
//        humA3.add(new HumedadAmbiental("3", 45, LocalDateTime.now().minusHours(2)));
//        humA3.add(new HumedadAmbiental("3", 50, LocalDateTime.now()));
//        carnivora.setHumedadesAmbientales(humA3);
//
//        listaPlantas.add(carnivora);
//    }

    public void actualizarDashboard(Planta planta){
        txtNombrePlanta.setText(planta.getNombre());

        // Última temperatura
        double temp = planta.getTemperaturas()
                .get(planta.getTemperaturas().size() - 1)
                .getValor();

        // Última humedad del suelo
        double humSuelo = planta.getHumedadesSuelo()
                .get(planta.getHumedadesSuelo().size() - 1)
                .getValor();

        // Última humedad ambiental
        double humAmb = planta.getHumedadesAmbientales()
                .get(planta.getHumedadesAmbientales().size() - 1)
                .getValor();

        txtTemperatura.setText(temp + " °C");
        txtHumedadSuelo.setText(humSuelo + " %");
        txtHumedadAmbiental.setText(humAmb + " %");

        // Actualización gráfica
        int ancho = chartView.getWidth();
        int alto = chartView.getHeight();
        if (ancho == 0 || alto == 0) return;

        Bitmap bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(40);
        paint.setColor(Color.BLACK);

        canvas.drawColor(Color.parseColor("#E0F2F1"));
        canvas.drawText("Temperatura: " + temp + " °C", 50, 80, paint);
        canvas.drawText("Humedad del Suelo: " + humSuelo + " %", 50, 150, paint);
        canvas.drawText("Humedad Ambiental: " + humAmb + " %", 50, 220, paint);

        chartView.setImageBitmap(bitmap);
    }
}
