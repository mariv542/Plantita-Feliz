package com.example.happyplant.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class appLogger {

    private DatabaseReference dbRef;
    private String userId;

    public appLogger(String userId) {
        this.userId = userId;
        dbRef = FirebaseDatabase.getInstance().getReference("logsApp");
    }

    public void logEvent(String evento, String detalles) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Map<String, Object> log = new HashMap<>();
        log.put("usuario", userId);
        log.put("evento", evento);
        log.put("detalles", detalles);
        log.put("timestamp", timestamp);

        dbRef.push().setValue(log);
    }
}

