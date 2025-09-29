package com.example.happyplant.model;

import java.time.LocalDateTime;

public class Humedad {
    private String plantaId;
    private double valor;
    private LocalDateTime fechaHora;

    public String getPlantaId() {return plantaId; }
    public void setPlantaId(String plantaId) { this.plantaId = plantaId; }

    public double getValor(){return valor;}
    public void setValor(double valor) { this.valor = valor;}

    public LocalDateTime getFechaHora() {return fechaHora;}
    public void setFechaHora(LocalDateTime fechaHora) {this.fechaHora = fechaHora;}
}
