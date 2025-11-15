package com.backend.tpi_backend.servicio_contenedores.dto;

import lombok.Data;

@Data
public class ContenedorPendienteDTO {
    private Integer id;
    private Double pesoKg;
    private Double volumenM3;
    private String estado;
    private Integer ubicacionActualId;
    private String clienteNombre;
    private Integer solicitudId;
}
