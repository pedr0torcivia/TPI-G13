package com.backend.tpi_backend.servicio_transporte.dto;

import lombok.Data;

@Data
public class SolicitudDTO {
    private Integer numero;
    private Integer origenId;
    private Integer destinoId;
    private Integer tarifaId;
    private Integer contenedorId;
}
