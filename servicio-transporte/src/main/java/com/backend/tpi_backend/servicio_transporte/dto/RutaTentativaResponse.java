package com.backend.tpi_backend.servicio_transporte.dto;

import lombok.Data;
import java.util.List;

@Data
public class RutaTentativaResponse {
    private List<TramoTentativoDTO> tramos;
    private double distanciaTotalKm;
    private double tiempoTotalHoras;
    private float costoTotalEstimado;
}
