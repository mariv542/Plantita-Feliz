package com.example.happyplant.controller;

import com.example.happyplant.model.Planta;
import com.example.happyplant.repository.PlantaRepository;
import com.google.firebase.database.ValueEventListener;

public class PlantaController {

    private final PlantaRepository repository;

    public PlantaController(String userId) {
        repository = new PlantaRepository(userId);
    }

    public void guardarPlanta(Planta planta) {
        repository.guardarPlanta(planta);
    }

    public void obtenerPlantas(ValueEventListener listener) {
        repository.obtenerPlantas(listener);
    }
}
