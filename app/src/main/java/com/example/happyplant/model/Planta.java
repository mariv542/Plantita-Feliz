package com.example.happyplant.model;

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
    public Planta (){}
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
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Parametros getParametros() { return parametros; }
    public void setParametros(Parametros parametros) { this.parametros = parametros; }

    public Map<String, Temperatura> getTemperaturas() { return temperaturas; }
    public void setTemperaturas(Map<String, Temperatura> temperaturas) { this.temperaturas = temperaturas; }

    public Map<String, HumedadSuelo> getHumedadesSuelo() { return humedadesSuelo; }
    public void setHumedadesSuelo(Map<String, HumedadSuelo> humedadesSuelo) { this.humedadesSuelo = humedadesSuelo; }

    public Map<String, HumedadAmbiental> getHumedadesAmbientales() { return humedadesAmbientales; }
    public void setHumedadesAmbientales(Map<String, HumedadAmbiental> humedadesAmbientales) { this.humedadesAmbientales = humedadesAmbientales; }

    public Map<String, NivelAgua> getNivelesAgua() { return nivelesAgua; }
    public void setNivelesAgua(Map<String, NivelAgua> nivelesAgua) { this.nivelesAgua = nivelesAgua; }

    public Map<String, Alerta> getAlertas() { return alertas; }
    public void setAlertas(Map<String, Alerta> alertas) { this.alertas = alertas; }



}
