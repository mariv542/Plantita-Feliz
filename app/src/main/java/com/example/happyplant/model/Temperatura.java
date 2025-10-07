package com.example.happyplant.model;

import java.time.LocalDateTime;

public class Temperatura {
    private String plantId;
    private double valor;
    private LocalDateTime fechaHora;

    public Temperatura (){}
    public Temperatura (String plantId, double valor, LocalDateTime fechaHora){
        this.plantId = plantId;
        this.valor = valor;
        this.fechaHora = fechaHora;
    }
    //getters y setters

    public String getPlantId() { return plantId; }
    public void setPlantaId(String plantaId) { this.plantId = plantaId; }

    public double getValor() { return  valor; }
    public void getValor(double valor) { this.valor = valor; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

}
