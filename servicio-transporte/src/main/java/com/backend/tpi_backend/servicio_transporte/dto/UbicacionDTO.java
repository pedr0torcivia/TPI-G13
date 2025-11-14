package com.backend.tpi_backend.servicio_transporte.dto;

import lombok.Data;

@Data
public class UbicacionDTO {
    private Long id;
    private String direccion;
    private double lat;
    private double lng;
}