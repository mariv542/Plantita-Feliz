package com.example.happyplant.model;

import java.util.List;

public class Planta {
    private String id;
    private String nombre;
    private Parametros parametros;
    private List<Temperatura> temperaturas;
    private List<HumedadSuelo> humedadesSuelo;
    private List<HumedadAmbiental> humedadesAmbientales;
    private List<NivelAgua> nivelesAgua;
    private List<Alerta> alertas;
    public Planta (){}
    public Planta(String id,
                  String nombre,
                  Parametros parametros,
                  List<Temperatura> temperaturas,
                  List<HumedadSuelo> humedadesSuelo,
                  List<HumedadAmbiental> humedadesAmbientales,
                  List<NivelAgua> nivelesAgua,
                  List<Alerta> alertas) {
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

    public List<Temperatura> getTemperaturas() { return temperaturas; }
    public void setTemperaturas(List<Temperatura> temperaturas) { this.temperaturas = temperaturas; }

    public List<HumedadSuelo> getHumedadesSuelo() { return humedadesSuelo; }
    public void setHumedadesSuelo(List<HumedadSuelo> humedadesSuelo) { this.humedadesSuelo = humedadesSuelo; }

    public List<HumedadAmbiental> getHumedadesAmbientales() { return humedadesAmbientales; }
    public void setHumedadesAmbientales(List<HumedadAmbiental> humedadesAmbientales) { this.humedadesAmbientales = humedadesAmbientales; }

    public List<NivelAgua> getNivelesAgua() { return nivelesAgua; }
    public void setNivelesAgua(List<NivelAgua> nivelesAgua) { this.nivelesAgua = nivelesAgua; }

    public List<Alerta> getAlertas() { return alertas; }
    public void setAlertas(List<Alerta> alertas) { this.alertas = alertas; }



}
