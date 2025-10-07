package com.example.happyplant.model;

import java.time.LocalDateTime;

public class NivelAgua {
    private String plantaId;
    private double litros;
    private LocalDateTime fechaHora;

    public NivelAgua (){}
    public NivelAgua(String plantaId, double litros, LocalDateTime fechaHora){
        this.plantaId = plantaId;
        this.litros = litros;
        this.fechaHora = fechaHora;
    }

    public String getPlantaId(){ return  plantaId;}
    public void setPlantaId(String plantaId) {this.plantaId = plantaId;}

    public double getLitros(){ return litros;}
    public void setLitros(double litros){this.litros = litros;}
    public LocalDateTime getFechaHora(){return fechaHora;}
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora;}
}
