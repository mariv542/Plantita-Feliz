package com.example.happyplant.model;

import java.time.LocalDateTime;

public class HumedadSuelo{
    private String plantaId;
    private double valor;
    private LocalDateTime fechaHora;

    public HumedadSuelo(){}
    public HumedadSuelo(String plantaId, double  valor, LocalDateTime fechaHora){
        this.plantaId = plantaId;
        this.valor = valor;
        this.fechaHora = fechaHora;
    }

    public String getPlantaId() {return plantaId; }
    public void setPlantaId(String plantaId) { this.plantaId = plantaId; }

    public double getValor(){return valor;}
    public void setValor(double valor) { this.valor = valor;}

    public LocalDateTime getFechaHora() {return fechaHora;}
    public void setFechaHora(LocalDateTime fechaHora) {this.fechaHora = fechaHora;}
}
