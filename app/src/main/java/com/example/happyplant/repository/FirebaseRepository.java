package com.example.happyplant.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseRepository {

    private final FirebaseDatabase database;

    public FirebaseRepository() {

        // Inicializa la referencia principal
        database = FirebaseDatabase.getInstance("https://happy-plant-44668-default-rtdb.firebaseio.com/");
    }

    public DatabaseReference getReference(String path) {
        return database.getReference(path);
    }

}
