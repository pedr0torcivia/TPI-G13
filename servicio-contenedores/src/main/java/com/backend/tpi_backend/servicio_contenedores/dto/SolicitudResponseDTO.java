package com.backend.tpi_backend.servicio_contenedores.dto;

import lombok.Data;

@Data
public class SolicitudResponseDTO {

    private Integer numero;        // ID de la solicitud
    private Integer clienteId;
    private Integer contenedorId;
    private Integer estadoId;

    private Integer origenId;
    private Integer destinoId;
    private Integer tarifaId;

    private Integer rutaId;        // puede ser null si aún no se asignó
}
