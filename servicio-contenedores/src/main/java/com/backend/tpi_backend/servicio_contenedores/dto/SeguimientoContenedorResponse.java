package com.backend.tpi_backend.servicio_contenedores.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SeguimientoContenedorResponse {
    private String estado;
    private Integer ubicacionId;
    private LocalDateTime fechaHora;
}
