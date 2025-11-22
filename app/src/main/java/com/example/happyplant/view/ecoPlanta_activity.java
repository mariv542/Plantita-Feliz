package com.example.happyplant.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.happyplant.R;
import com.example.happyplant.model.Planta;
import com.example.happyplant.model.Usuario;
import com.example.happyplant.repository.PlantaRepository;
import com.example.happyplant.repository.UsuarioRepository;
import com.example.happyplant.utils.GPSHelper;
import com.example.happyplant.utils.appLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ecoPlanta_activity extends AppCompatActivity {

    private EditText etEcoPlanta_nombrePlanta, etEcoPlanta_idMaceta;
    private Button btnEcoPlanta_guardar;
    private Spinner spinnerMaceta;
    private UsuarioRepository usuarioRepo;
    private PlantaRepository plantaRepo;
    private Usuario usuarioLogueado;
    private FirebaseAuth auth;
    private TextView txtGPS;
    private ImageButton btnEcoPlanta_regresar;
    private GPSHelper gpsHelper;
    private ArrayList<String> listaIds;
    private appLogger appLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_planta);

        // Inicializar logger con UID o "anonimo"
        String uid = "anonimo";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) uid = firebaseUser.getUid();
        appLogger = new appLogger(uid);
        appLogger.logEvent("abrirPantalla", "Usuario abrió ecoPlanta_activity");

        // Inicialización de vistas
        etEcoPlanta_nombrePlanta = findViewById(R.id.et_ecoPlanta_nombrePlanta);
        etEcoPlanta_idMaceta = findViewById(R.id.et_ecoPlanta_idMaceta);
        btnEcoPlanta_guardar = findViewById(R.id.btn_ecoPlanta_guardar);
        spinnerMaceta = findViewById(R.id.spinnerMacetas);
        txtGPS = findViewById(R.id.txtGPS);
        btnEcoPlanta_regresar = findViewById(R.id.btn_ecoPlanta_regresar);

        // Firebase
        auth = FirebaseAuth.getInstance();
        usuarioRepo = new UsuarioRepository();
        cargarUsuarioLogueado();

        // GPS
        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
            appLogger.logEvent("gpsObtenido", "Ciudad detectada: " + ciudad);
        });

        // Botón guardar
        btnEcoPlanta_guardar.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnEcoPlanta_guardar");
            guardarPlanta();
        });

        // Botón regresar
        btnEcoPlanta_regresar.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnEcoPlanta_regresar");
            startActivity(new Intent(ecoPlanta_activity.this, menu_activity.class));
        });

        // Spinner de IDs
        listaIds = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listaIds);
        spinnerMaceta.setAdapter(adapter);

        DatabaseReference refPlantas = FirebaseDatabase.getInstance().getReference("plantas");
        refPlantas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaIds.clear();
                listaIds.add("Seleccionar maceta...");
                for (DataSnapshot plantaSnap : snapshot.getChildren()) {
                    listaIds.add(plantaSnap.getKey());
                }
                adapter.notifyDataSetChanged();
                appLogger.logEvent("spinnerCargado", "Spinner de macetas cargado con " + (listaIds.size() - 1) + " IDs");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                appLogger.logEvent("errorBD", "Error al cargar IDs: " + error.getMessage());
            }
        });

        spinnerMaceta.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position > 0) {
                    String idSeleccionado = listaIds.get(position);
                    etEcoPlanta_idMaceta.setText(idSeleccionado);
                    appLogger.logEvent("seleccionMaceta", "ID de maceta seleccionado: " + idSeleccionado);
                } else {
                    etEcoPlanta_idMaceta.setText("");
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                etEcoPlanta_idMaceta.setText("");
                appLogger.logEvent("seleccionMaceta", "No se seleccionó ninguna maceta");
            }
        });
    }

    private void cargarUsuarioLogueado() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            appLogger.logEvent("errorUsuario", "No hay usuario autenticado");
            return;
        }

        String email = firebaseUser.getEmail();
        appLogger.logEvent("usuarioAutenticado", "Email: " + email);

        usuarioRepo.getReference("usuarios")
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                usuarioLogueado = userSnap.getValue(Usuario.class);
                                usuarioLogueado.setId(userSnap.getKey());
                                appLogger.logEvent("cargarUsuario", "Usuario cargado: " + usuarioLogueado.getEmail());

                                plantaRepo = new PlantaRepository(usuarioLogueado.getId());
                                appLogger.logEvent("inicializarRepo", "PlantaRepository inicializado con ID usuario: " + usuarioLogueado.getId());
                            }
                        } else {
                            appLogger.logEvent("errorUsuario", "Usuario no encontrado en Firebase");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        appLogger.logEvent("errorBD", "Error al consultar usuario: " + error.getMessage());
                    }
                });
    }

    private boolean validarCampos() {
        String idPlanta = etEcoPlanta_idMaceta.getText().toString().trim();
        String nombrePersonalizado = etEcoPlanta_nombrePlanta.getText().toString().trim();
        boolean valido = true;

        if (idPlanta.isEmpty()) {
            etEcoPlanta_idMaceta.setError("ID requerido");
            appLogger.logEvent("validacion", "ID vacío");
            valido = false;
        }
        if (nombrePersonalizado.isEmpty()) {
            etEcoPlanta_nombrePlanta.setError("Nombre requerido");
            appLogger.logEvent("validacion", "Nombre vacío");
            valido = false;
        }

        return valido;
    }

    private String obtenerIdUsuario() {
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();
            appLogger.logEvent("obtenerIdUsuario", "UID obtenido: " + uid);
            return uid;
        } else {
            appLogger.logEvent("errorUsuario", "Usuario no autenticado");
            Toast.makeText(this, "Error: usuario no autenticado.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void guardarPlanta() {
        if (!validarCampos()) return;

        String idPlanta = etEcoPlanta_idMaceta.getText().toString().trim();
        String idUsuario = obtenerIdUsuario();
        if (idUsuario == null) return;

        appLogger.logEvent("guardarPlanta", "Guardando planta con ID " + idPlanta + " para usuario " + idUsuario);

        FirebaseDatabase.getInstance().getReference("plantas").child(idPlanta)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Planta planta = snapshot.getValue(Planta.class);
                            if (planta == null) {
                                appLogger.logEvent("errorBD", "Error al leer planta desde Firebase");
                                Toast.makeText(ecoPlanta_activity.this, "Error al leer la planta.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            planta.setNombre(etEcoPlanta_nombrePlanta.getText().toString().trim());
                            plantaRepo.guardarPlanta(planta);

                            appLogger.logEvent("guardarPlanta", "Planta guardada correctamente: " + planta.getNombre());
                            Toast.makeText(ecoPlanta_activity.this, "Planta agregada correctamente.", Toast.LENGTH_SHORT).show();
                            etEcoPlanta_nombrePlanta.setText("");
                            etEcoPlanta_idMaceta.setText("");
                            etEcoPlanta_idMaceta.setBackgroundTintList(getColorStateList(R.color.teal_200));
                        } else {
                            appLogger.logEvent("errorGuardar", "No existe planta con ID " + idPlanta);
                            etEcoPlanta_idMaceta.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            Toast.makeText(ecoPlanta_activity.this, "No existe una planta con ese ID.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        appLogger.logEvent("errorBD", "Error al obtener planta: " + error.getMessage());
                        Toast.makeText(ecoPlanta_activity.this, "Error al obtener planta: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
