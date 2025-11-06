package com.example.happyplant.model;

public class HumedadAmbiental {
    private String plantaId;
    private double valor;
    private String fecha;

    public HumedadAmbiental() {}

    public HumedadAmbiental(String plantaId, double valor, String fecha) {
        this.plantaId = plantaId;
        this.valor = valor;
        this.fecha = fecha;
    }

    public String getPlantaId() { return plantaId; }
    public void setPlantaId(String plantaId) { this.plantaId = plantaId; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
