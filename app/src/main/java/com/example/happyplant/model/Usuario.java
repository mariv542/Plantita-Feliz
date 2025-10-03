package com.example.happyplant.model;

import java.util.List;

public class Usuario {
    private String id;
    private String nombre;
    private String email;
    private String passwordHash;

    private List<Planta> plantas;

    public String getId() {return id;}
    public void setId(String id){this.id = id;}

    public String getNombre(){return nombre;}
    public void setNombre(String nombre){ this.nombre = nombre;}

    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    public String getPasswordHash(){return passwordHash;}
    public void setPasswordHash(String passwordHash){this.passwordHash = passwordHash;}

}
