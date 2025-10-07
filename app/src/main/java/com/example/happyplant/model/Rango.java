package com.example.happyplant.model;

public class Rango {
    private String id;
    private double minimo;
    private double maximo;

    public Rango() {}

    public Rango(String id, double minimo, double maximo) {
        this.id = id;
        this.minimo = minimo;
        this.maximo = maximo;
    }

    public boolean estaDentro(double valor){
        return valor >= minimo && valor <= maximo;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public double getMinimo() {
        return minimo;
    }
    public void setMinimo(double minimo) {
        this.minimo = minimo;
    }

    public double getMaximo() {
        return maximo;
    }
    public void setMaximo(double maximo) {
        this.maximo = maximo;
    }
}
