package com.backend.tpi_backend.servicio_deposito.controller;

import com.backend.tpi_backend.servicio_deposito.model.Provincia;
import com.backend.tpi_backend.servicio_deposito.services.ProvinciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provincias")
@RequiredArgsConstructor
public class ProvinciaController {

    private final ProvinciaService provinciaService;

    @GetMapping
    public ResponseEntity<List<Provincia>> getAll() {
        return ResponseEntity.ok(provinciaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Provincia> getById(@PathVariable Integer id) {
        return provinciaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Provincia> create(@RequestBody Provincia provincia) {
        return ResponseEntity.ok(provinciaService.save(provincia));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Provincia> update(@PathVariable Integer id, @RequestBody Provincia provincia) {
        return ResponseEntity.ok(provinciaService.update(id, provincia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        provinciaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
