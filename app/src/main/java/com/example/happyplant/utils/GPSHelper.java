package com.example.happyplant.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;

public class GPSHelper {
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private Activity activity;

    public GPSHelper(Activity activity){
        this.activity = activity;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void obtenerUbicacion(final OnLocationReceived listener) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    listener.onReceived(location.getLatitude(), location.getLongitude());
                } else {
                    listener.onReceived(0, 0);
                }
            }
        });
    }

    public interface OnLocationReceived {
        void onReceived(double lat, double lon);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults, OnLocationReceived listener) {
        if (requestCode == REQUEST_LOCATION_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            obtenerUbicacion(listener);
        }
    }

    public String obtenerCiudad(double lat, double lon, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> direcciones = geocoder.getFromLocation(lat, lon, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                String ciudad = direcciones.get(0).getLocality();
                if (ciudad != null) return ciudad;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Ubicación desconocida";
    }

}

