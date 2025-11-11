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

import com.backend.tpi_backend.servicio_transporte.model.TramoEstado;
import com.backend.tpi_backend.servicio_transporte.services.TramoEstadoService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tramo-estados")
@RequiredArgsConstructor
public class TramoEstadoController {

    private final TramoEstadoService tramoEstadoService;

    @GetMapping
    public ResponseEntity<List<TramoEstado>> getAll() {
        return ResponseEntity.ok(tramoEstadoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TramoEstado> getById(@PathVariable Integer id) {
        return tramoEstadoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TramoEstado> create(@RequestBody TramoEstado tramoEstado) {
        return ResponseEntity.ok(tramoEstadoService.save(tramoEstado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TramoEstado> update(@PathVariable Integer id, @RequestBody TramoEstado tramoEstado) {
        return ResponseEntity.ok(tramoEstadoService.update(id, tramoEstado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        tramoEstadoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}