package com.backend.tpi_backend.servicio_transporte.client;

import com.backend.tpi_backend.servicio_transporte.dto.CalculoTarifaRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// El nombre del servicio de tarifas
// NOTA: Para eliminar la redundancia en el path, el Controlador de Tarifa usa /api/tarifas.
// El cliente Feign debe usar SOLO el sufijo del path.
@FeignClient(name = "servicio-tarifa") 
public interface TarifaClient {

    // ✅ CORRECCIÓN: Usar SOLO el sufijo del path expuesto por el controlador: /calcular
    @PostMapping("/api/tarifas/calcular") 
    Float calcularTarifa(@RequestBody CalculoTarifaRequest request);
    
    // ✅ CORRECCIÓN: Usar SOLO el sufijo del path expuesto por el controlador: /valor-combustible
    @GetMapping("/api/tarifas/valor-combustible")
    Float getValorLitroCombustible();
}