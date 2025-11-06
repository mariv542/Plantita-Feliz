package com.example.happyplant.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Alerta {
    private String plantaId;
    private String tipo;
    private String mensaje;
    private String fecha;

    public Alerta(){}
    public Alerta(String plantaId, String tipo, String mensaje, String fecha){
        this.plantaId = plantaId;
        this.tipo = tipo;
        this.mensaje = mensaje;
    }

    public String getPlantaId(){return plantaId;}
    public void setPlantaId(String plantaId){this.plantaId = plantaId;}
    public String getTipo(){return tipo;}
    public void setTipo(String tipo){this.tipo = tipo;}
    public String getMensaje(){return mensaje;}
    public void setMensaje(String mensaje){this.mensaje = mensaje;}

    public String getFechaHora(){return fecha;}
    public void setFechaHora(String fecha){this.fecha = fecha;}
}
