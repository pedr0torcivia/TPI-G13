package com.backend.tpi_backend.servicio_transporte.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.tpi_backend.servicio_transporte.model.Camion;
import com.backend.tpi_backend.servicio_transporte.services.CamionService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/camiones")
@RequiredArgsConstructor
public class CamionController {

    private final CamionService camionService;

    @GetMapping
    public ResponseEntity<List<Camion>> getAll() {
        return ResponseEntity.ok(camionService.findAll());
    }

    @GetMapping("/{dominio}")
    public ResponseEntity<Camion> getById(@PathVariable String dominio) {
        return camionService.findById(dominio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Camion> create(@RequestBody Camion camion) {
        return ResponseEntity.ok(camionService.save(camion));
    }

    @PutMapping("/{dominio}")
    public ResponseEntity<Camion> update(@PathVariable String dominio, @RequestBody Camion camion) {
        return ResponseEntity.ok(camionService.update(dominio, camion));
    }

    @DeleteMapping("/{dominio}")
    public ResponseEntity<Void> delete(@PathVariable String dominio) {
        camionService.deleteById(dominio);
        return ResponseEntity.noContent().build();
    }
}