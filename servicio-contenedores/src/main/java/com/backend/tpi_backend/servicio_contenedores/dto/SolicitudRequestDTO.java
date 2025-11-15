package com.backend.tpi_backend.servicio_contenedores.dto;

import lombok.Data;

/**
 * Este DTO (Data Transfer Object) se usa para recibir la petición
 * de un CLIENTE para crear una nueva solicitud.
 * Contiene los datos para crear el Contenedor y la Solicitud.
 */
@Data
public class SolicitudRequestDTO {

    // Datos del Cliente (para "registrar si no existe")
    // El email y el idKeycloak vendrán del Token JWT
    private String nombre;
    private String telefono;
    private String direccion;

    // Datos del Contenedor (para "creación del contenedor")
    private double pesoKg;
    private double volumenM3;

    // Datos de la Solicitud (los IDs de los otros servicios)
    private Integer origenId;
    private Integer destinoId;
    private Integer tarifaId;
}