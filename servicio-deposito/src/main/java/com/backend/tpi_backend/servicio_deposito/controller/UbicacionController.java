package com.backend.tpi_backend.servicio_deposito.controller;

import com.backend.tpi_backend.servicio_deposito.model.Ubicacion;
import com.backend.tpi_backend.servicio_deposito.services.UbicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
@RequiredArgsConstructor
public class UbicacionController {

    private final UbicacionService ubicacionService;

    @GetMapping
    public ResponseEntity<List<Ubicacion>> getAll() {
        return ResponseEntity.ok(ubicacionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ubicacion> getById(@PathVariable Long id) {
        return ubicacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Ubicacion> create(@RequestBody Ubicacion ubicacion) {
        return ResponseEntity.ok(ubicacionService.save(ubicacion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ubicacion> update(@PathVariable Long id, @RequestBody Ubicacion ubicacion) {
        return ResponseEntity.ok(ubicacionService.update(id, ubicacion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ubicacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
