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
import android.util.Log;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private ImageButton btnAbrirCamara;

    private Uri fotoUri;

    private TextView txtGPS;
    private GPSHelper gpsHelper;

    private ImageButton btnUsuario_regresar;
    private TextInputEditText editNombre, editPassword, editPasswordActual;
    private TextView txtEmail;
    private Button btnGuardarCambios;

    private FirebaseAuth auth;
    private DatabaseReference refUsuarios;
    private UsuarioRepository usuarioRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_usuario);

        usuarioRepository = new UsuarioRepository();

        btnUsuario_regresar = findViewById(R.id.btn_ecoUsuario_regresar);
        editNombre = findViewById(R.id.editNombre);
        editPassword = findViewById(R.id.editPassword);
        txtEmail = findViewById(R.id.txtEmail);
        btnGuardarCambios = findViewById(R.id.btn_ecoUsuario_GuardarCambios);
        editPasswordActual = findViewById(R.id.edit_ecoUsuario_PasswordActual);

        auth = FirebaseAuth.getInstance();
        refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");

        cargarDatosUsuario();
        cargarUltimaFotoGuardada();

        btnGuardarCambios.setOnClickListener(v -> guardarCambios());

        btnUsuario_regresar.setOnClickListener(v -> {
            Intent intent = new Intent(ecoUsuario_activity.this, perfil_activity.class);
            startActivity(intent);
        });

        txtGPS = findViewById(R.id.txtGPS);
        gpsHelper = new GPSHelper(this);

        gpsHelper.obtenerUbicacion((lat, lon) -> {
            String ciudad = gpsHelper.obtenerCiudad(lat, lon, this);
            txtGPS.setText("Ciudad: " + ciudad);
        });

        imgPerfil = findViewById(R.id.imgPerfilUsuario);
        btnAbrirCamara = findViewById(R.id.btnCambiarFotoPerfil);

        btnAbrirCamara.setOnClickListener(v -> verificarPermisoCamara());
    }

    private void verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISO_CAMARA
            );
        } else {
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
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirCamara() {
        try {
            File foto = crearArchivoImagen();
            if (foto == null) {
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

        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir la cámara", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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

                    mostrarFotoCircular(bitmap); // 🔥 Foto circular
                    guardarFotoBase64(bitmap);

                } catch (Exception e) {
                    Toast.makeText(this, "Error al procesar imagen", Toast.LENGTH_SHORT).show();
                }
            } else {
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
                    int limite = 5; // 🔹 Limite de fotos

                    if (count >= limite) {
                        // eliminar la foto más vieja
                        fotosRef.child("0").removeValue();

                        // reordenar los índices
                        for (int i = 1; i < limite; i++) {
                            String vieja = snapshot.child(String.valueOf(i)).getValue(String.class);
                            fotosRef.child(String.valueOf(i - 1)).setValue(vieja);
                        }

                        fotosRef.child(String.valueOf(limite - 1)).setValue(base64);

                    } else {
                        fotosRef.child(String.valueOf(count)).setValue(base64);
                    }

                    Toast.makeText(ecoUsuario_activity.this, "Foto guardada ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

        } catch (Exception e) {
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
                    mostrarFotoCircular(bmp); // 🔥 Foto circular
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private File crearArchivoImagen() {
        try {
            String nombreArchivo = "foto_perfil";
            File directorio = getExternalFilesDir("Images");

            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            return new File(directorio, nombreArchivo + ".jpg");

        } catch (Exception e) {
            Log.e("EcoUsuario", "Error creando archivo: " + e.getMessage());
            return null;
        }
    }

    private void cargarDatosUsuario() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        String email = firebaseUser.getEmail();

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
                                Log.d("EcoUsuario", "Usuario cargado: " + nombre + " (UID: " + uid + ")");
                            }
                        } else {
                            Toast.makeText(ecoUsuario_activity.this,
                                    "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ecoUsuario_activity.this,
                                "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show();
                        Log.e("EcoUsuario", "Error Firebase", error.toException());
                    }
                });
    }

    private void guardarCambios() {
        String nuevoNombre = editNombre.getText().toString().trim();
        String nuevaPassword = editPassword.getText().toString().trim();
        String passwordActual = editPasswordActual.getText().toString().trim();

        if (nuevoNombre.isEmpty() || passwordActual.isEmpty() || nuevaPassword.isEmpty()) {
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
                Toast.makeText(ecoUsuario_activity.this, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ecoUsuario_activity.this, "Error al actualizar nombre: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarPasswordDecorativa(String uid) {
        Map<String, Object> actualizacion = new HashMap<>();
        actualizacion.put("passwordHash", "**********");

        refUsuarios.child(uid).updateChildren(actualizacion)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Indicador de contraseña actualizado", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void guardarPassword(String passwordActual, String nuevaPassword) {
        usuarioRepository.actualizarPassword(passwordActual, nuevaPassword, new UsuarioRepository.RegistroCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ecoUsuario_activity.this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();

                String uid = auth.getCurrentUser().getUid();
                actualizarPasswordDecorativa(uid);

                limpiarCamposContrasena();
            }

            @Override
            public void onFailure(String error) {
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
