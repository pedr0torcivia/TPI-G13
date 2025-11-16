package com.backend.tpi_backend.servicio_transporte.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Apunta al microservicio que tiene la entidad Deposito
@FeignClient(name = "servicio-deposito") 
public interface DepositoClient {

    /**
     * Endpoint para obtener el costo de estadía diario de un depósito específico.
     * Usaremos el ID de la Ubicación (origen/destino del tramo) para encontrar el Depósito.
     * Nota: Asumimos que la Ubicacion (ID) es la misma PK que la tabla de Ubicacion.
     * @param ubicacionId El ID de la ubicación asociada al depósito (origen/destino del tramo).
     * @return El costo diario de estadía (Double).
     */
    @GetMapping("/api/depositos/ubicacion/{ubicacionId}/costo-estadia")
    Double getCostoEstadiaByUbicacionId(@PathVariable Integer ubicacionId);
}