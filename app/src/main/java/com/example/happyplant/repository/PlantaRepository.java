package com.example.happyplant.repository;

import androidx.annotation.NonNull;

import com.example.happyplant.model.Planta;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class PlantaRepository extends FirebaseRepository{

    private final DatabaseReference plantasRef;

    public PlantaRepository(String userId) {
        super();
        plantasRef = getReference("usuarios/" + userId + "/plantas");
    }

    public void guardarPlanta(Planta planta) {
        if (planta.getId() == null) {
            planta.setId(plantasRef.push().getKey());
        }
        plantasRef.child(planta.getId()).setValue(planta);
    }

    public void obtenerPlantas(ValueEventListener listener) {
        plantasRef.addValueEventListener(listener);
    }

    public void obtenerPlantaPorId(String id, ValueEventListener listener) {
        plantasRef.child(id).addListenerForSingleValueEvent(listener);
    }

    public void eliminarPlanta(String id) {
        plantasRef.child(id).removeValue();
    }
}
