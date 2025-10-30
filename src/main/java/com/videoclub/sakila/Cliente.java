package com.videoclub.sakila;

public class Cliente {
    
    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private Integer activo;

    public Cliente(){
        
    }
    
    public Cliente (Integer id, String nombre, String apellido, String email, Integer activo){
        this.id = id;
        this.nombre=nombre;
        this.apellido=apellido;
        this.email=email;
        this.activo=activo;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public Integer getActivo() {
        return activo;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setActivo(Integer activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Cliente [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", email=" + email
                + ", activo=" + activo + "]";
    }

    
    
}
