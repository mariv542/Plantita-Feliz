package com.example.happyplant.model;

import java.time.LocalDateTime;

public class Temperatura {
    private String plantId;
    private double valor;
    private String fechaHora;

    public Temperatura (){}
    public Temperatura (String plantId, double valor, String fechaHora){
        this.plantId = plantId;
        this.valor = valor;
        this.fechaHora = fechaHora;
    }
    //getters y setters

    public String getPlantId() { return plantId; }
    public void setPlantaId(String plantaId) { this.plantId = plantaId; }

    public double getValor() { return  valor; }
    public void getValor(double valor) { this.valor = valor; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

}
