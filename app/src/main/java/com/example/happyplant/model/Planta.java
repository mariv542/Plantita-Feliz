package com.example.happyplant.model;

import java.util.List;

public class Planta {
    private String id;
    private String nombre;
    private Parametros parametros;
    private List<Temperatura> temperaturas;
    private List<Humedad> humedad;
    private List<NivelAgua> nivelAguas;
    private List<Consumo> consumos;
    private List<EstadoActuador> actuadores;
    private List<Alerta> alertas;

    public Planta (){}
    public Planta (String id, String nombre, Parametros parametros,
                   List<Temperatura> temperaturas,
                   List<Humedad> humedad,
                   List<NivelAgua> nivelAguas,
                   List<Consumo> consumos,
                   List<EstadoActuador> actuadores,
                   List<Alerta> alertas){
        this.id = id;
        this.nombre = nombre;
        this.parametros = parametros;
        this.temperaturas = temperaturas;
        this.humedad = humedad;
        this.nivelAguas = nivelAguas;
        this.consumos =  consumos;
        this.actuadores = actuadores;
        this.alertas = alertas;

    }

    //getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    //nombre
    public String getNombre() {return  nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    //parametros
    public Parametros getParametros() { return parametros;}
    public void setParametros ( Parametros parametros) { this.parametros = parametros; }
    //Temperatura
    public List<Temperatura> getTemperaturas() { return temperaturas; }
    public void setTemperaturas(List<Temperatura> temperaturas) { this.temperaturas = temperaturas; }
    //Humedad
    public List<Humedad> getHumedad() {return humedad; }
    public void setHumedad(List<Humedad> humedad) { this.humedad = humedad; }
    //NivelAgua
    public List<NivelAgua> getNivelAguas() {return nivelAguas; }
    public void setNivelAguas(List<NivelAgua> nivelAgua) { this.nivelAguas = nivelAguas; }
    //Consumo
    public List<Consumo> getConsumos(){ return consumos;}
    public void setConsumos(List<Consumo> consumos) { this.consumos = consumos; }
    //EstadoActuador
    public List<EstadoActuador> getActuadores(){ return actuadores;}
    public void setActuadores(List<EstadoActuador> actuadores) { this.actuadores = actuadores; }
    //Alerta
    public List<Alerta> getAlertas(){ return alertas;}
    public void setAlertas(List<Alerta> alertas) { this.alertas = alertas; }



}
