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

import com.backend.tpi_backend.servicio_transporte.model.Ruta;
import com.backend.tpi_backend.servicio_transporte.services.RutaService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaService rutaService;

    @GetMapping
    public ResponseEntity<List<Ruta>> getAll() {
        return ResponseEntity.ok(rutaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ruta> getById(@PathVariable Integer id) {
        return rutaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Ruta> create(@RequestBody Ruta ruta) {
        return ResponseEntity.ok(rutaService.save(ruta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ruta> update(@PathVariable Integer id, @RequestBody Ruta ruta) {
        return ResponseEntity.ok(rutaService.update(id, ruta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        rutaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}