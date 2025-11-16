package com.backend.tpi_backend.servicio_transporte.client;

import com.backend.tpi_backend.servicio_transporte.dto.UbicacionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Ajusta "servicio-deposito" si tu servicio se llama de otra forma en Eureka/Consul
@FeignClient(name = "servicio-deposito") 
public interface UbicacionClient {

    // ✅ CORRECCIÓN: Se añade la barra inicial para asegurar que el path sea /api/ubicaciones/{id}
    @GetMapping("/api/ubicaciones/{id}") 
    UbicacionDTO obtenerPorId(@PathVariable Integer id);
}