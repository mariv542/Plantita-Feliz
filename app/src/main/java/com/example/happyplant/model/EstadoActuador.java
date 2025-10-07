package com.example.happyplant.model;

import java.time.LocalDateTime;

public class EstadoActuador {
    private String plataId;
    private String nombre;
    private LocalDateTime fechaHora;

    public EstadoActuador(){}
    public EstadoActuador(String plataId, String nombre,LocalDateTime fechaHora){
        this.plataId = plataId;
        this.nombre = nombre;
        this.fechaHora = fechaHora;
    }

    public String getPlataId(){return  plataId;}
    public void setPlataId(String plataId){this.plataId = plataId;}

    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre = nombre;}
    public LocalDateTime getFechaHora(){return fechaHora;}
    public void setFechaHora(LocalDateTime fechaHora){this.fechaHora = fechaHora;}
}
