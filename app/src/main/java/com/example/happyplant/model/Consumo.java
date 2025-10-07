package com.example.happyplant.model;

import java.time.LocalDateTime;

public class Consumo {
    private String plataId;
    private double valor;
    private LocalDateTime fechaHora;

    public Consumo(){}
    public Consumo(String plataId, double valor, LocalDateTime fechaHora){
        this.plataId = plataId;
        this.valor = valor;
        this.fechaHora = fechaHora;
    }

    public String getPlataId() {return plataId;}
    public void setPlataId(String plataId){this.plataId = plataId;}
    public double getValor(){return valor;}
    public void setValor(double valor){this.valor = valor;}
    public LocalDateTime getFechaHora(){return fechaHora;}
    public void setFechaHora(LocalDateTime fechaHora){this.fechaHora = fechaHora; }

}
