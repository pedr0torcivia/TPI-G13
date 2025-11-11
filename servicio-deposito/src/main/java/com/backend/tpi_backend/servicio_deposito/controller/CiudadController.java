package com.backend.tpi_backend.servicio_deposito.controller;

import com.backend.tpi_backend.servicio_deposito.model.Ciudad;
import com.backend.tpi_backend.servicio_deposito.services.CiudadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ciudades")
@RequiredArgsConstructor
public class CiudadController {

    private final CiudadService ciudadService;

    @GetMapping
    public ResponseEntity<List<Ciudad>> getAll() {
        return ResponseEntity.ok(ciudadService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ciudad> getById(@PathVariable Integer id) {
        return ciudadService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Ciudad> create(@RequestBody Ciudad ciudad) {
        return ResponseEntity.ok(ciudadService.save(ciudad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ciudad> update(@PathVariable Integer id, @RequestBody Ciudad ciudad) {
        return ResponseEntity.ok(ciudadService.update(id, ciudad));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ciudadService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
