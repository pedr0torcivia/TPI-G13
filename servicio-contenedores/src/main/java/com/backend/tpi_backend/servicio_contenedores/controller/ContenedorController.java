package com.backend.tpi_backend.servicio_contenedores.controller;

import com.backend.tpi_backend.servicio_contenedores.model.Contenedor;
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

    // Para crear, pasamos el ID del cliente y la ubicación por Query Params
    @PostMapping
    public ResponseEntity<Contenedor> save(@RequestBody Contenedor contenedor,
                                           @RequestParam Integer clienteId,
                                           @RequestParam Integer ubicacionId) {
        Contenedor guardado = service.save(contenedor, clienteId, ubicacionId);
        return ResponseEntity.status(201).body(guardado);
    }

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