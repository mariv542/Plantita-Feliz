package com.example.happyplant.model;

import java.time.LocalDateTime;

public class Alerta {
    private String plantaId;
    private String tipo;
    private String mensaje;
    private LocalDateTime fechaHora;

    public String getPlantaId(){return plantaId;}
    public void setPlantaId(String plantaId){this.plantaId = plantaId;}
    public String getTipo(){return tipo;}
    public void setTipo(String tipo){this.tipo = tipo;}
    public String getMensaje(){return mensaje;}
    public void setMensaje(String mensaje){this.mensaje = mensaje;}

    public LocalDateTime getFechaHora(){return fechaHora;}
    public void setFechaHora(LocalDateTime fechaHora){this.fechaHora = fechaHora;}
}
