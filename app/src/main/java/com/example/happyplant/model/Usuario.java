package com.example.happyplant.model;

import java.util.List;
import java.util.Map;

public class Usuario {
    private String id;
    private String nombre;
    private String email;
    private String passwordHash;
    private Map<String, Planta> plantas;

    public Usuario () {}

    public Usuario (String id, String nombre, String email, String passwordHash,  Map<String, Planta> plantas ) {
        this.id = id ;
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.plantas = plantas;
    }



    public String getId() {return id;}
    public void setId(String id){this.id = id;}

    public String getNombre(){return nombre;}
    public void setNombre(String nombre){ this.nombre = nombre;}

    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    public String getPasswordHash(){return passwordHash;}
    public void setPasswordHash(String passwordHash){this.passwordHash = passwordHash;}
    public Map<String, Planta> getPlantas() { return plantas; }
    public void setPlantas(Map<String, Planta> plantas) { this.plantas = plantas; }

}
