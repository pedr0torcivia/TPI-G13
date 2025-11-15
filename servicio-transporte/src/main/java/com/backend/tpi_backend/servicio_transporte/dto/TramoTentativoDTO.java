package com.backend.tpi_backend.servicio_transporte.dto;

import lombok.Data;

@Data
public class TramoTentativoDTO {
    private Integer origenId;
    private Integer destinoId;
    private double distanciaKm;
    private double tiempoHoras;
    private float costoEstimado;
}
