package com.example.happyplant.model;

import java.time.LocalDateTime;

public class EstadoActuador {
    private String plataId;
    private String nombre;
    private boolean encendido;
    private LocalDateTime fechaHora;

    public String getPlataId(){return  plataId;}
    public void setPlataId(String plataId){this.plataId = plataId;}

    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre = nombre;}
    public boolean isEncendido() {return encendido;}
    public void setEncendido(boolean encendido){this.encendido = encendido;}

    public LocalDateTime getFechaHora(){return fechaHora;}
    public void setFechaHora(LocalDateTime fechaHora){this.fechaHora = fechaHora;}
}
