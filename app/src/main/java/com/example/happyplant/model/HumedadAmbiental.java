package com.example.happyplant.model;

import java.time.LocalDateTime;

public class HumedadAmbiental {
    private String plantaId;
    private double valor;
    private String fechaHora;

    public HumedadAmbiental() {}

    public HumedadAmbiental(String plantaId, double valor, String fechaHora) {
        this.plantaId = plantaId;
        this.valor = valor;
        this.fechaHora = fechaHora;
    }

    public String getPlantaId() { return plantaId; }
    public void setPlantaId(String plantaId) { this.plantaId = plantaId; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }
}
