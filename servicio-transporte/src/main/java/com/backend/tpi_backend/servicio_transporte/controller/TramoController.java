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
@RequestMapping("/api/tramos")
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
    public ResponseEntity<Tramo> create(@RequestBody Tramo tramo) { 
        return ResponseEntity.ok(tramoService.save(tramo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tramo> update(@PathVariable Integer id, @RequestBody Tramo tramo) { 
        return ResponseEntity.ok(tramoService.update(id, tramo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        tramoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoint para asignar camión ---
    // NOTA: Cambié la URL a "/{idTramo}/asignar-camion/{dominioCamion}" 
    //       para ser consistente con el mapping "/tramos" de la clase.
    @PutMapping("/{idTramo}/asignar-camion/{dominioCamion}")
    public ResponseEntity<String> asignarCamion(
            @PathVariable Integer idTramo,
            @PathVariable String dominioCamion
    ) {
        String msg = tramoService.asignarCamion(idTramo, dominioCamion);
        return ResponseEntity.ok(msg);
    }

    @PutMapping("/{id}/inicio")
    public ResponseEntity<Tramo> iniciarTramo(@PathVariable Integer id) {
        return ResponseEntity.ok(tramoService.iniciarTramo(id));
    }

    @PutMapping("/{id}/fin")
    public ResponseEntity<Tramo> finalizarTramo(@PathVariable Integer id) {
        return ResponseEntity.ok(tramoService.finalizarTramo(id));
    }

    // --- 🚀 NUEVO ENDPOINT PARA CALCULAR TARIFA ---
    
    /**
     * Calcula la tarifa final de un tramo específico.
     * Este método llama a OSRM para la distancia, al servicio de contenedores
     * para peso/volumen y al servicio de tarifas para el cálculo final.
     *
     * @param id El ID del Tramo
     * @return La tarifa calculada como un valor float.
     */
    @GetMapping("/{id}/tarifa")
    public ResponseEntity<Float> calcularTarifa(@PathVariable Integer id) {
        // Llama al método que creamos en TramoService
        float tarifa = tramoService.obtenerTarifaParaTramo(id);
        return ResponseEntity.ok(tarifa);
    }
}