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

    @GetMapping
    public ResponseEntity<List<Solicitud>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
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