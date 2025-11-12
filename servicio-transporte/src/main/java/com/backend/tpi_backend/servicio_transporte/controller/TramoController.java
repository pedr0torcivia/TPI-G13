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
import org.springframework.web.bind.annotation.RequestBody; 

import com.backend.tpi_backend.servicio_transporte.model.Tramo;
import com.backend.tpi_backend.servicio_transporte.services.TramoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tramos")
@RequiredArgsConstructor
public class TramoController {

    private final TramoService tramoService;

    @GetMapping
    public ResponseEntity<List<Tramo>> getAll() {
        return ResponseEntity.ok(tramoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tramo> getById(@PathVariable Integer id) {
        return tramoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tramo> create(@RequestBody Tramo tramo) { // <-- USA EL @RequestBody CORREGIDO
        return ResponseEntity.ok(tramoService.save(tramo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tramo> update(@PathVariable Integer id, @RequestBody Tramo tramo) { // <-- USA EL @RequestBody CORREGIDO
        return ResponseEntity.ok(tramoService.update(id, tramo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        tramoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idTramo}/asignar-camion/{dominio}")
    public ResponseEntity<Tramo> asignarCamion(@PathVariable Integer idTramo, @PathVariable String dominio) {
        return ResponseEntity.ok(tramoService.asignarCamion(idTramo, dominio));
    }

    @PutMapping("/{id}/inicio")
    public ResponseEntity<Tramo> iniciarTramo(@PathVariable Integer id) {
        return ResponseEntity.ok(tramoService.iniciarTramo(id));
    }

    @PutMapping("/{id}/fin")
    public ResponseEntity<Tramo> finalizarTramo(@PathVariable Integer id) {
    return ResponseEntity.ok(tramoService.finalizarTramo(id));
    }
}