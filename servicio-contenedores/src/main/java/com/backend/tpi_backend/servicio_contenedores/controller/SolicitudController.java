package com.backend.tpi_backend.servicio_contenedores.controller;

import com.backend.tpi_backend.servicio_contenedores.model.Solicitud;
import com.backend.tpi_backend.servicio_contenedores.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService service;

    // --- MÉTODO ACTUALIZADO ---
    @GetMapping
    public ResponseEntity<List<Solicitud>> findAll(
            @RequestParam(required = false) String estado) {
        
        List<Solicitud> solicitudes;

        if (estado != null && !estado.isEmpty()) {
            // Si se provee un filtro 'estado', lo usamos
            solicitudes = service.findByEstadoNombre(estado);
        } else {
            // Si no, devolvemos todo
            solicitudes = service.findAll();
        }
        return ResponseEntity.ok(solicitudes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

        // --- NUEVO ENDPOINT (Para resolver el TODO de TramoService) ---
    /**
     * Devuelve solo el ID del contenedor asociado a una solicitud.
     * Usado por servicio-transporte (via Feign) para saber a qué
     * contenedor debe actualizarle el estado.
     */
    @GetMapping("/{id}/contenedorId")
    public ResponseEntity<Integer> getContenedorIdBySolicitudId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findContenedorIdBySolicitudId(id));
    }

    // Para crear, pasamos el ID del contenedor por Query Param
    @PostMapping
    public ResponseEntity<Solicitud> save(@RequestBody Solicitud solicitud,
                                          @RequestParam Integer contenedorId) {
        Solicitud guardada = service.save(solicitud, contenedorId);
        return ResponseEntity.status(201).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Solicitud> update(@PathVariable Integer id, @RequestBody Solicitud solicitud) {
        return ResponseEntity.ok(service.update(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}