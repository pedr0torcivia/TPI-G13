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

import com.backend.tpi_backend.servicio_transporte.model.TramoTipo;
import com.backend.tpi_backend.servicio_transporte.services.TramoTipoService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tramo-tipos")
@RequiredArgsConstructor
public class TramoTipoController {

    private final TramoTipoService tramoTipoService;

    @GetMapping
    public ResponseEntity<List<TramoTipo>> getAll() {
        return ResponseEntity.ok(tramoTipoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TramoTipo> getById(@PathVariable Integer id) {
        return tramoTipoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TramoTipo> create(@RequestBody TramoTipo tramoTipo) {
        return ResponseEntity.ok(tramoTipoService.save(tramoTipo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TramoTipo> update(@PathVariable Integer id, @RequestBody TramoTipo tramoTipo) {
        return ResponseEntity.ok(tramoTipoService.update(id, tramoTipo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        tramoTipoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
