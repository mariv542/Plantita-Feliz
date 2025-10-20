package com.example.happyplant.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.happyplant.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

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

    public void guardarUsuario(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(usuariosRef.push().getKey());
        }
        usuariosRef.child(usuario.getId()).setValue(usuario);
    }

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

    public void obtenerUsuarios(ValueEventListener listener) {
        usuariosRef.addValueEventListener(listener);
    }

    public void obtenerUsuarioPorId(String id, ValueEventListener listener) {
        usuariosRef.child(id).addListenerForSingleValueEvent(listener);
    }

    public void eliminarUsuario(String id) {
        usuariosRef.child(id).removeValue();
    }

}

