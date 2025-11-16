package com.backend.tpi_backend.servicio_transporte.dto;

import lombok.Data;

@Data
public class CamionDTO {
    private String dominio;
    private double capacidadPesoKg;
    private double capacidadVolumenM3;
    private double costoKm;
    private double consumoCombustibleKm;
    private boolean disponibilidad;
}