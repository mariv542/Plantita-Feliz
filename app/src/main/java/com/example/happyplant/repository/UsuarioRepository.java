package com.example.happyplant.repository;

import androidx.annotation.NonNull;

import com.example.happyplant.model.Usuario;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UsuarioRepository extends FirebaseRepository{

    private final DatabaseReference usuariosRef;

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

