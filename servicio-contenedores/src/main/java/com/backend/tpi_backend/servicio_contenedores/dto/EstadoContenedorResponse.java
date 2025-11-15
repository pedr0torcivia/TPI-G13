package com.backend.tpi_backend.servicio_contenedores.dto;

import lombok.Data;
import java.util.List;

@Data
public class EstadoContenedorResponse {
    private Integer contenedorId;
    private String estadoActual;
    private Integer ubicacionActualId;
    private List<SeguimientoContenedorResponse> historial;
}
