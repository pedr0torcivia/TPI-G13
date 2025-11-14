package com.backend.tpi_backend.servicio_transporte.client;

import com.backend.tpi_backend.servicio_transporte.dto.CalculoTarifaRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// El nombre del servicio de tarifas
@FeignClient(name = "servicio-tarifa") 
public interface TarifaClient {

    // El endpoint que creamos en el TarifaController
    @PostMapping("/tarifa/calcular") 
    Float calcularTarifa(@RequestBody CalculoTarifaRequest request);
}