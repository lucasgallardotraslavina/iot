package com.example.iot;

public class Mascota {
    private String nombre_mascota;
    private String imagen_mascota;
    private String mascota_id;
    private String color;
    private String direccion;
    private String especie;
    private String fecha_nacimiento;
    private String nombre_dueño;
    private String raza;
    private String sexo;
    private String telefono;

    // Constructor vacío necesario para Firebase
    public Mascota() {}

    // Constructor con parámetros opcionales
    public Mascota(String nombre_mascota, String imagen_mascota, String mascota_id, String color, String direccion,
                   String especie, String fecha_nacimiento, String nombre_dueño, String raza, String sexo, String telefono) {
        this.nombre_mascota = nombre_mascota;
        this.imagen_mascota = imagen_mascota;
        this.mascota_id = mascota_id;
        this.color = color;
        this.direccion = direccion;
        this.especie = especie;
        this.fecha_nacimiento = fecha_nacimiento;
        this.nombre_dueño = nombre_dueño;
        this.raza = raza;
        this.sexo = sexo;
        this.telefono = telefono;
    }

    // Getters y Setters para cada campo
    public String getNombre_mascota() {
        return nombre_mascota;
    }

    public void setNombre_mascota(String nombre_mascota) {
        this.nombre_mascota = nombre_mascota;
    }

    public String getImagen_mascota() {
        return imagen_mascota;
    }

    public void setImagen_mascota(String imagen_mascota) {
        this.imagen_mascota = imagen_mascota;
    }

    public String getMascota_id() {
        return mascota_id;
    }

    public void setMascota_id(String mascota_id) {
        this.mascota_id = mascota_id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getNombre_dueño() {
        return nombre_dueño;
    }

    public void setNombre_dueño(String nombre_dueño) {
        this.nombre_dueño = nombre_dueño;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
