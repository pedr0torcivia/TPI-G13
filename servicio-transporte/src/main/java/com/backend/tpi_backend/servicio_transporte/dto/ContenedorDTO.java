package com.backend.tpi_backend.servicio_transporte.dto;

public class ContenedorDTO {

    private Integer id;
    private double pesoKg;
    private double volumenM3;

    // getters & setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public double getPesoKg() { return pesoKg; }
    public void setPesoKg(double pesoKg) { this.pesoKg = pesoKg; }

    public double getVolumenM3() { return volumenM3; }
    public void setVolumenM3(double volumenM3) { this.volumenM3 = volumenM3; }
}