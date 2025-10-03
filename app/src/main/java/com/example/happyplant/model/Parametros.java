package com.example.happyplant.model;

public class Parametros {
    private String id;
    private String plantaId;
    private Rango rangoHumedadSuelo;
    private Rango rangoTemperatura;
    private Rango rangoHumedadAmbiental;
    private Rango rangoNivelAgua;

    //Getter y setters
    //ID
    public String getId() {return id; }
    public void setId(String id ) {this.id = id; }
    //PlataId
    public String getPlantaId() {return plantaId; }
    public void setPlantaId(String plantaId ) {this.plantaId = plantaId; }

    //RangoHumedadSuelo
    public Rango getRangoHumedadSuelo() {return rangoHumedadSuelo; }
    public void setRangoHumedadSuelo(Rango rangoHumedadSuelo ) {this.rangoHumedadSuelo = rangoHumedadSuelo; }
    //RangoTemperatura
    public Rango getRangoTemperatura() {return rangoTemperatura; }
    public void setRangoTemperatura(Rango rangoTemperatura ) {this.rangoTemperatura = rangoTemperatura; }
    //RangoHumedadAmbiental
    public Rango getRangoHumedadAmbiental() {return rangoHumedadAmbiental; }
    public void setRangoHumedadAmbiental(Rango rangoHumedadAmbiental ) {this.rangoHumedadAmbiental = rangoHumedadAmbiental; }
    //RangoNivelAgua
    public Rango getRangoNivelAgua() {return rangoNivelAgua; }
    public void setRangoNivelAgua(Rango rangoNivelAgua ) {this.rangoNivelAgua = rangoNivelAgua; }
}
