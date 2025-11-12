package com.backend.tpi_backend.servicio_tarifa.controller;

import com.backend.tpi_backend.servicio_tarifa.model.Tarifa;
import com.backend.tpi_backend.servicio_tarifa.services.TarifaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
@RequiredArgsConstructor

public class TarifaController {
    private final TarifaService tarifaService;

    @GetMapping
    public ResponseEntity<List<Tarifa>> getAll() {
        List<Tarifa> tarifas = tarifaService.findAll();
        return ResponseEntity.ok(tarifas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> getById(@PathVariable Integer id) {
        return tarifaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tarifa> create(@RequestBody Tarifa tarifa) {
        Tarifa savedTarifa = tarifaService.save(tarifa);
        return ResponseEntity.ok(savedTarifa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarifa> update(@PathVariable Integer id, @RequestBody Tarifa tarifa) {
        try {
            Tarifa updatedTarifa = tarifaService.update(id, tarifa);
            return ResponseEntity.ok(updatedTarifa);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        tarifaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}