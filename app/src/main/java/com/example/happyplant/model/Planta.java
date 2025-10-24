package com.example.happyplant.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.List;
import java.util.Map;

public class Planta {
    private String id;
    private String nombre;
    private Parametros parametros;
    private Map<String, Temperatura> temperaturas;
    private Map<String, HumedadSuelo> humedadesSuelo;
    private Map<String, HumedadAmbiental> humedadesAmbientales;
    private Map<String, NivelAgua> nivelesAgua;
    private Map<String, Alerta> alertas;

    public Planta() {
    }

    public Planta(String id,
                  String nombre,
                  Parametros parametros,
                  Map<String, Temperatura> temperaturas,
                  Map<String, HumedadSuelo> humedadesSuelo,
                  Map<String, HumedadAmbiental> humedadesAmbientales,
                  Map<String, NivelAgua> nivelesAgua,
                  Map<String, Alerta> alertas) {
        this.id = id;
        this.nombre = nombre;
        this.parametros = parametros;
        this.temperaturas = temperaturas;
        this.humedadesSuelo = humedadesSuelo;
        this.humedadesAmbientales = humedadesAmbientales;
        this.nivelesAgua = nivelesAgua;
        this.alertas = alertas;
    }

    //getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Parametros getParametros() {
        return parametros;
    }

    public void setParametros(Parametros parametros) {
        this.parametros = parametros;
    }

    public Map<String, Temperatura> getTemperaturas() {
        return temperaturas;
    }

    public void setTemperaturas(Map<String, Temperatura> temperaturas) {
        this.temperaturas = temperaturas;
    }

    public Map<String, HumedadSuelo> getHumedadesSuelo() {
        return humedadesSuelo;
    }

    public void setHumedadesSuelo(Map<String, HumedadSuelo> humedadesSuelo) {
        this.humedadesSuelo = humedadesSuelo;
    }

    public Map<String, HumedadAmbiental> getHumedadesAmbientales() {
        return humedadesAmbientales;
    }

    public void setHumedadesAmbientales(Map<String, HumedadAmbiental> humedadesAmbientales) {
        this.humedadesAmbientales = humedadesAmbientales;
    }

    public Map<String, NivelAgua> getNivelesAgua() {
        return nivelesAgua;
    }

    public void setNivelesAgua(Map<String, NivelAgua> nivelesAgua) {
        this.nivelesAgua = nivelesAgua;
    }

    public Map<String, Alerta> getAlertas() {
        return alertas;
    }

    public void setAlertas(Map<String, Alerta> alertas) {
        this.alertas = alertas;
    }

    public void cargarDatosDesdeSnapshot(DataSnapshot ds) {
        // Temperaturas
        GenericTypeIndicator<Map<String, Temperatura>> tipoTemp =
                new GenericTypeIndicator<Map<String, Temperatura>>() {};
        Map<String, Temperatura> tempMap = ds.child("temperaturas").getValue(tipoTemp);
        if (tempMap != null) this.temperaturas = tempMap;

        // Humedad del suelo
        GenericTypeIndicator<Map<String, HumedadSuelo>> tipoHumSuelo =
                new GenericTypeIndicator<Map<String, HumedadSuelo>>() {};
        Map<String, HumedadSuelo> humSueloMap = ds.child("humedadesSuelo").getValue(tipoHumSuelo);
        if (humSueloMap != null) this.humedadesSuelo = humSueloMap;

        // Humedad ambiental
        GenericTypeIndicator<Map<String, HumedadAmbiental>> tipoHumAmb =
                new GenericTypeIndicator<Map<String, HumedadAmbiental>>() {};
        Map<String, HumedadAmbiental> humAmbMap = ds.child("humedadesAmbientales").getValue(tipoHumAmb);
        if (humAmbMap != null) this.humedadesAmbientales = humAmbMap;

        // Niveles de agua
        GenericTypeIndicator<Map<String, NivelAgua>> tipoNivelAgua =
                new GenericTypeIndicator<Map<String, NivelAgua>>() {};
        Map<String, NivelAgua> nivelAguaMap = ds.child("nivelesAgua").getValue(tipoNivelAgua);
        if (nivelAguaMap != null) this.nivelesAgua = nivelAguaMap;

        // Alertas
        GenericTypeIndicator<Map<String, Alerta>> tipoAlertas =
                new GenericTypeIndicator<Map<String, Alerta>>() {};
        Map<String, Alerta> alertasMap = ds.child("alertas").getValue(tipoAlertas);
        if (alertasMap != null) this.alertas = alertasMap;
    }


}
