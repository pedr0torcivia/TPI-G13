package com.backend.tpi_backend.servicio_deposito.controller;

import com.backend.tpi_backend.servicio_deposito.model.Estadia;
import com.backend.tpi_backend.servicio_deposito.services.EstadiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/depositos")
@RequiredArgsConstructor
public class EstadiaController {

    private final EstadiaService estadiaService;

    // Obtener todas las estadías de un depósito
    @GetMapping("/{id}/contenedores")
    public ResponseEntity<List<Estadia>> getContenedoresEnDeposito(@PathVariable Integer id) {
        return ResponseEntity.ok(estadiaService.findByDeposito(id));
    }

    // Calcular costo de estadía por cantidad de días
    @GetMapping("/{id}/estadias")
    public ResponseEntity<Double> calcularCosto(@PathVariable Integer id, @RequestParam int dias) {
        double costo = estadiaService.calcularCostoEstadia(id, dias);
        return ResponseEntity.ok(costo);
    }

    // Calcular costo según fechas de una estadía específica
    @GetMapping("/{id}/estadias/{idEstadia}")
    public ResponseEntity<Double> calcularCostoPorFechas(@PathVariable Integer id, @PathVariable Long idEstadia) {
        double costo = estadiaService.calcularCostoPorFechas(id, idEstadia);
        return ResponseEntity.ok(costo);
    }
}
