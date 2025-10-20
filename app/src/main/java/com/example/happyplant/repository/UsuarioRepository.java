package com.example.happyplant.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.happyplant.model.Usuario;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UsuarioRepository extends FirebaseRepository{

    private final DatabaseReference usuariosRef;

    public interface RegistroCallback {
        void onSuccess();
        void onFailure(String error);
    }
    public UsuarioRepository() {
        super();
        usuariosRef = getReference("usuarios");
    }

    // Guardar usuario en Realtime Database
    public void guardarUsuario(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(usuariosRef.push().getKey());
        }
        usuariosRef.child(usuario.getId()).setValue(usuario);
    }

    // Registrar usuario en Firebase Auth y guardar su perfil
    public void registrarUsuarioConAuth(String nombre, String email, String password, RegistroCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            Usuario usuario = new Usuario(
                                    firebaseUser.getUid(),
                                    nombre,
                                    email,
                                    "",
                                    new HashMap<>()
                            );
                            guardarUsuario(usuario);
                            callback.onSuccess();
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    // Obtener lista de usuarios (por ahora no es necesario)
    public void obtenerUsuarios(ValueEventListener listener) {
        usuariosRef.addValueEventListener(listener);
    }

    // Obtener usuario por ID ( por ahora no es necesario)
    public void obtenerUsuarioPorId(String id, ValueEventListener listener) {
        usuariosRef.child(id).addListenerForSingleValueEvent(listener);
    }

    // Eliminar usuario ( por ahora no es necesario)
    public void eliminarUsuario(String id) {
        usuariosRef.child(id).removeValue();
    }

    public void actualizarNombre(String uid, String nuevoNombre, RegistroCallback callback) {
        Map<String, Object> actualizacion = new HashMap<>();
        actualizacion.put("nombre", nuevoNombre);

        usuariosRef.child(uid).updateChildren(actualizacion)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void actualizarPassword(String passwordActual, String nuevaPassword, RegistroCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuario no autenticado");
            return;
        }

        // Reautenticación
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), passwordActual);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(nuevaPassword)
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            } else {
                callback.onFailure("Error al reautenticar: " + task.getException().getMessage());
            }
        });
    }

}



