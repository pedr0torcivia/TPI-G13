package com.backend.tpi_backend.servicio_tarifa.dto;

import lombok.Data;

@Data
public class CalculoTarifaRequest {

    private float volumen;
    private float peso;
    private float distanciaKm;

    private float valorLitroCombustible;
    private float consumoCombustible;

    private float costoKmCamion;   
    private int diasOcupados;
    private float costoEstadiaDiario;

    private float cargoGestion;    
}