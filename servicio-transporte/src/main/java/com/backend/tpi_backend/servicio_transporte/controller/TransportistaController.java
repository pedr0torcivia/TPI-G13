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

import com.backend.tpi_backend.servicio_transporte.model.Tramo;
import com.backend.tpi_backend.servicio_transporte.model.Transportista;
import com.backend.tpi_backend.servicio_transporte.services.TransportistaService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transportistas")
@RequiredArgsConstructor
public class TransportistaController {

    private final TransportistaService transportistaService;

    @GetMapping
    public ResponseEntity<List<Transportista>> getAll() {
        return ResponseEntity.ok(transportistaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transportista> getById(@PathVariable Integer id) {
        return transportistaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Transportista> create(@RequestBody Transportista transportista) {
        return ResponseEntity.ok(transportistaService.save(transportista));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transportista> update(@PathVariable Integer id, @RequestBody Transportista transportista) {
        return ResponseEntity.ok(transportistaService.update(id, transportista));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        transportistaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

     // Transportista puede ver todos los tramos asignados a sus camiones
    @GetMapping("/{id}/tramos")
    public ResponseEntity<List<Tramo>> getTramosDelTransportista(@PathVariable Integer id) {
        List<Tramo> tramos = transportistaService.obtenerTramosAsignados(id);
        return ResponseEntity.ok(tramos);
    }
}