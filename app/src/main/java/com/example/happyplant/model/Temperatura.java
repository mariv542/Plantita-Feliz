package com.example.happyplant.model;

import java.time.LocalDateTime;

public class Temperatura {
    private String plantId;
    private double valor;
    private String fecha;

    public Temperatura (){}
    public Temperatura (String plantId, double valor, String fecha){
        this.plantId = plantId;
        this.valor = valor;
        this.fecha = fecha;
    }
    //getters y setters

    public String getPlantId() { return plantId; }
    public void setPlantaId(String plantaId) { this.plantId = plantaId; }

    public double getValor() { return  valor; }
    public void getValor(double valor) { this.valor = valor; }

    public String getFechaHora() { return fecha; }
    public void setFechaHora(String fecha) { this.fecha = fecha; }

}
