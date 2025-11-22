package com.example.happyplant.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.happyplant.R;
import com.example.happyplant.repository.UsuarioRepository;
import com.example.happyplant.utils.GPSHelper;
import com.example.happyplant.utils.appLogger;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ecoUsuario_activity extends AppCompatActivity {

    private static final int REQUEST_CAMARA = 100;
    private static final int REQUEST_PERMISO_CAMARA = 200;

    private ImageView imgPerfil;
    private ImageButton btnAbrirCamara, btnUsuario_regresar;
    private Uri fotoUri;
    private TextView txtGPS;
    private GPSHelper gpsHelper;
    private TextInputEditText editNombre, editPassword, editPasswordActual;
    private TextView txtEmail;
    private Button btnGuardarCambios;

    private FirebaseAuth auth;
    private DatabaseReference refUsuarios;
    private UsuarioRepository usuarioRepository;
    private appLogger appLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_usuario);

        // Inicializar AppLogger con UID o "anonimo"
        String uid = "anonimo";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        appLogger = new appLogger(uid);
        appLogger.logEvent("abrirPantalla", "Usuario abrió ecoUsuario_activity");

        // Inicializar vistas
        imgPerfil = findViewById(R.id.imgPerfilUsuario);
        btnAbrirCamara = findViewById(R.id.btnCambiarFotoPerfil);
        btnUsuario_regresar = findViewById(R.id.btn_ecoUsuario_regresar);
        editNombre = findViewById(R.id.editNombre);
        editPassword = findViewById(R.id.editPassword);
        editPasswordActual = findViewById(R.id.edit_ecoUsuario_PasswordActual);
        txtEmail = findViewById(R.id.txtEmail);
        btnGuardarCambios = findViewById(R.id.btn_ecoUsuario_GuardarCambios);
        txtGPS = findViewById(R.id.txtGPS);

        // Repositorio y Firebase
        usuarioRepository = new UsuarioRepository();
        auth = FirebaseAuth.getInstance();
        refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");

        // Cargar datos
        cargarDatosUsuario();
        cargarUltimaFotoGuardada();

        // GPS
        gpsHelper = new GPSHelper(this);
        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
            appLogger.logEvent("gpsObtenido", "Ciudad detectada: " + ciudad);
        });

        // Botones
        btnUsuario_regresar.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnUsuario_regresar");
            startActivity(new Intent(ecoUsuario_activity.this, perfil_activity.class));
        });

        btnGuardarCambios.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnGuardarCambios");
            guardarCambios();
        });

        btnAbrirCamara.setOnClickListener(v -> {
            appLogger.logEvent("clickBoton", "Presionó btnAbrirCamara");
            verificarPermisoCamara();
        });
    }

    private void verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISO_CAMARA
            );
            appLogger.logEvent("permisoCamara", "Solicitando permiso de cámara");
        } else {
            appLogger.logEvent("permisoCamara", "Permiso de cámara ya concedido");
            abrirCamara();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISO_CAMARA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                appLogger.logEvent("permisoCamara", "Permiso de cámara concedido");
                abrirCamara();
            } else {
                appLogger.logEvent("permisoCamara", "Permiso de cámara denegado");
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirCamara() {
        try {
            File foto = crearArchivoImagen();
            if (foto == null) {
                appLogger.logEvent("errorFoto", "No se pudo crear el archivo de imagen");
                Toast.makeText(this, "No se pudo crear el archivo", Toast.LENGTH_SHORT).show();
                return;
            }

            fotoUri = FileProvider.getUriForFile(
                    this,
                    "com.example.happyplant.provider",
                    foto
            );

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivityForResult(intent, REQUEST_CAMARA);
            appLogger.logEvent("abrirCamara", "Cámara abierta");
        } catch (Exception e) {
            appLogger.logEvent("errorFoto", "Error al abrir la cámara: " + e.getMessage());
            Toast.makeText(this, "Error al abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMARA && resultCode == RESULT_OK) {
            if (fotoUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(fotoUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    mostrarFotoCircular(bitmap);
                    guardarFotoBase64(bitmap);
                    appLogger.logEvent("fotoGuardada", "Foto tomada y guardada correctamente");
                } catch (Exception e) {
                    appLogger.logEvent("errorFoto", "Error al procesar imagen: " + e.getMessage());
                    Toast.makeText(this, "Error al procesar imagen", Toast.LENGTH_SHORT).show();
                }
            } else {
                appLogger.logEvent("errorFoto", "fotoUri es null");
                Toast.makeText(this, "Error: fotoUri es null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mostrarFotoCircular(Bitmap bitmap) {
        Glide.with(this)
                .load(bitmap)
                .transform(new CircleCrop())
                .placeholder(R.drawable.icon_user)
                .into(imgPerfil);
    }

    private void guardarFotoBase64(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            String base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            String uid = auth.getCurrentUser().getUid();
            DatabaseReference fotosRef = refUsuarios.child(uid).child("fotosPerfil");

            fotosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long count = snapshot.getChildrenCount();
                    int limite = 5;

                    if (count >= limite) {
                        fotosRef.child("0").removeValue();
                        for (int i = 1; i < limite; i++) {
                            String vieja = snapshot.child(String.valueOf(i)).getValue(String.class);
                            fotosRef.child(String.valueOf(i - 1)).setValue(vieja);
                        }
                        fotosRef.child(String.valueOf(limite - 1)).setValue(base64);
                        appLogger.logEvent("fotoGuardada", "Foto guardada y reordenadas fotos existentes");
                    } else {
                        fotosRef.child(String.valueOf(count)).setValue(base64);
                        appLogger.logEvent("fotoGuardada", "Foto guardada en posición " + count);
                    }

                    Toast.makeText(ecoUsuario_activity.this, "Foto guardada", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    appLogger.logEvent("errorBD", "Error al guardar foto: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            appLogger.logEvent("errorFoto", "Error al codificar y guardar foto: " + e.getMessage());
            Toast.makeText(this, "Error al guardar ", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarUltimaFotoGuardada() {
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference fotosRef = refUsuarios.child(uid).child("fotosPerfil");

        fotosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                long count = snapshot.getChildrenCount();
                String ultima = snapshot.child(String.valueOf(count - 1)).getValue(String.class);
                if (ultima != null) {
                    byte[] bytes = Base64.decode(ultima, Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    mostrarFotoCircular(bmp);
                    appLogger.logEvent("fotoCargada", "Última foto cargada y mostrada");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                appLogger.logEvent("errorBD", "Error al cargar última foto: " + error.getMessage());
            }
        });
    }

    private File crearArchivoImagen() {
        try {
            String nombreArchivo = "foto_perfil";
            File directorio = getExternalFilesDir("Images");
            if (!directorio.exists()) directorio.mkdirs();
            return new File(directorio, nombreArchivo + ".jpg");
        } catch (Exception e) {
            appLogger.logEvent("errorFoto", "Error creando archivo: " + e.getMessage());
            return null;
        }
    }

    private void cargarDatosUsuario() {
        if (auth.getCurrentUser() == null) return;
        String email = auth.getCurrentUser().getEmail();

        refUsuarios.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                String nombre = userSnap.child("nombre").getValue(String.class);
                                String password = userSnap.child("passwordHash").getValue(String.class);

                                editNombre.setText(nombre);
                                txtEmail.setText(email);
                                editPassword.setText(password);

                                String uid = userSnap.getKey();
                                appLogger.logEvent("cargarUsuario", "Usuario cargado: " + nombre + " (UID: " + uid + ")");
                            }
                        } else {
                            appLogger.logEvent("errorUsuario", "Usuario no encontrado en Firebase");
                            Toast.makeText(ecoUsuario_activity.this,
                                    "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        appLogger.logEvent("errorBD", "Error al cargar datos del usuario: " + error.getMessage());
                    }
                });
    }

    private void guardarCambios() {
        String nuevoNombre = editNombre.getText().toString().trim();
        String nuevaPassword = editPassword.getText().toString().trim();
        String passwordActual = editPasswordActual.getText().toString().trim();

        if (nuevoNombre.isEmpty() || passwordActual.isEmpty() || nuevaPassword.isEmpty()) {
            appLogger.logEvent("validacion", "Campos incompletos para guardar cambios");
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        guardarNombre(nuevoNombre);
        guardarPassword(passwordActual, nuevaPassword);
    }

    private void guardarNombre(String nuevoNombre) {
        String uid = auth.getCurrentUser().getUid();
        usuarioRepository.actualizarNombre(uid, nuevoNombre, new UsuarioRepository.RegistroCallback() {
            @Override
            public void onSuccess() {
                appLogger.logEvent("guardarNombre", "Nombre actualizado correctamente: " + nuevoNombre);
                Toast.makeText(ecoUsuario_activity.this, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                appLogger.logEvent("errorGuardar", "Error al actualizar nombre: " + error);
                Toast.makeText(ecoUsuario_activity.this, "Error al actualizar nombre: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarPasswordDecorativa(String uid) {
        Map<String, Object> actualizacion = new HashMap<>();
        actualizacion.put("passwordHash", "**********");

        refUsuarios.child(uid).updateChildren(actualizacion)
                .addOnSuccessListener(aVoid -> appLogger.logEvent("guardarPasswordDecorativa", "Indicador de contraseña actualizado"))
                .addOnFailureListener(e -> appLogger.logEvent("errorGuardar", "Error al actualizar indicador de contraseña: " + e.getMessage()));
    }

    private void guardarPassword(String passwordActual, String nuevaPassword) {
        usuarioRepository.actualizarPassword(passwordActual, nuevaPassword, new UsuarioRepository.RegistroCallback() {
            @Override
            public void onSuccess() {
                appLogger.logEvent("guardarPassword", "Contraseña actualizada correctamente");
                Toast.makeText(ecoUsuario_activity.this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                String uid = auth.getCurrentUser().getUid();
                actualizarPasswordDecorativa(uid);
                limpiarCamposContrasena();
            }

            @Override
            public void onFailure(String error) {
                appLogger.logEvent("errorGuardar", "Error al actualizar contraseña: " + error);
                Toast.makeText(ecoUsuario_activity.this, "Error al actualizar contraseña: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        limpiarCamposContrasena();
    }

    private void limpiarCamposContrasena() {
        editPasswordActual.setText("");
        editPassword.setText("");
    }
}
