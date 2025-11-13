package com.backend.tpi_backend.servicio_transporte.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping; // <-- IMPORTAR
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.backend.tpi_backend.servicio_transporte.dto.ContenedorDTO;

// name = "servicio-contenedores" (arbitrario, para logs)
// url = "http://localhost:8081" (El puerto del servicio que consumimos)
@FeignClient(name = "servicio-contenedores", url = "http://localhost:8081")
public interface ContenedoresClient {

    /**
     * Llama al endpoint: PUT http://localhost:8081/api/contenedores/{id}/estado
     */
    @PutMapping("/api/contenedores/{id}/estado")
    ResponseEntity<Void> updateEstado(
            @PathVariable("id") Integer id,
            @RequestParam("estadoId") Integer estadoId,
            @RequestParam("ubicacionId") Integer ubicacionId
    );

    /**
     * Llama al endpoint: GET http://localhost:8081/api/solicitudes/{id}/contenedorId
     * Obtiene el ID del contenedor (que necesita updateEstado) a partir del ID de la solicitud.
     */
    @GetMapping("/api/solicitudes/{id}/contenedorId")
    Integer getContenedorIdBySolicitudId(@PathVariable("id") Integer id);

    // Llama al endpoint: GET http://localhost:8081/api/contenedores/{id}
    @GetMapping("/api/contenedores/{id}")
    ContenedorDTO getContenedor(@PathVariable("id") Integer id);
}