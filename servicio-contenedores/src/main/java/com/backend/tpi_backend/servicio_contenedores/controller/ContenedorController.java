package com.backend.tpi_backend.servicio_contenedores.controller;

import com.backend.tpi_backend.servicio_contenedores.model.Contenedor;
import com.backend.tpi_backend.servicio_contenedores.model.ContenedorEstado; // Importar
import com.backend.tpi_backend.servicio_contenedores.service.ContenedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenedores")
@RequiredArgsConstructor
public class ContenedorController {

    private final ContenedorService service;

    @GetMapping
    public ResponseEntity<List<Contenedor>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contenedor> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/{id}/estado")
    public ResponseEntity<ContenedorEstado> getEstadoById(@PathVariable Integer id) {
        ContenedorEstado estado = service.findEstadoById(id);
        return ResponseEntity.ok(estado);
    }

    // --- NUEVO ENDPOINT ---
    // PUT /api/contenedores/1/estado?estadoId=3&ubicacionId=101
    /**
     * Actualiza solo el estado de un contenedor (usado por otros microservicios).
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<Contenedor> updateEstado(
            @PathVariable Integer id,
            @RequestParam Integer estadoId,
            @RequestParam Integer ubicacionId) {
        
        Contenedor actualizado = service.updateEstado(id, estadoId, ubicacionId);
        return ResponseEntity.ok(actualizado);
    }


    // Para crear, pasamos el ID del cliente y la ubicación por Query Params
    @PostMapping
    public ResponseEntity<Contenedor> save(@RequestBody Contenedor contenedor,
                                           @RequestParam Integer clienteId,
                                           @RequestParam Integer ubicacionId) {
        Contenedor guardado = service.save(contenedor, clienteId, ubicacionId);
        return ResponseEntity.status(201).body(guardado);
    }

    // PUT /api/contenedores/1 (Para editar peso/volumen)
    @PutMapping("/{id}")
    public ResponseEntity<Contenedor> update(@PathVariable Integer id, @RequestBody Contenedor contenedor) {
        return ResponseEntity.ok(service.update(id, contenedor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}