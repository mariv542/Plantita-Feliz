package com.example.happyplant.model;

import java.time.LocalDateTime;

public class NivelAgua {
    private String plantaId;
    private double litros;
    private String fecha;

    public NivelAgua (){}
    public NivelAgua(String plantaId, double litros, String fecha){
        this.plantaId = plantaId;
        this.litros = litros;
        this.fecha = fecha;
    }

    public String getPlantaId(){ return  plantaId;}
    public void setPlantaId(String plantaId) {this.plantaId = plantaId;}

    public double getLitros(){ return litros;}
    public void setLitros(double litros){this.litros = litros;}
    public String getFechaHora(){return fecha;}
    public void setFechaHora(String fecha) { this.fecha = fecha;}
}
