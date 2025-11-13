package com.backend.tpi_backend.servicio_deposito.controller;

import com.backend.tpi_backend.servicio_deposito.model.Estadia;
import com.backend.tpi_backend.servicio_deposito.services.EstadiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estadias")
@RequiredArgsConstructor
public class EstadiaController {

    private final EstadiaService estadiaService;

    // 🔹 Listar contenedores actualmente en un depósito
    @GetMapping("/{idDeposito}/contenedores")
    public ResponseEntity<List<Estadia>> getContenedoresEnDeposito(@PathVariable Integer idDeposito) {
        return ResponseEntity.ok(estadiaService.findContenedoresActivos(idDeposito));
    }

    // 🔹 Filtrar contenedores por estado (ej. “en_estadia”, “listo”)
    @GetMapping("/{idDeposito}/contenedores/filtrar")
    public ResponseEntity<List<Estadia>> filtrarContenedores(
            @PathVariable Integer idDeposito,
            @RequestParam String estado
    ) {
        return ResponseEntity.ok(estadiaService.findContenedoresByEstado(idDeposito, estado));
    }

    // 🔹 Listar contenedores listos para continuar viaje
    @GetMapping("/{idDeposito}/contenedores/listos")
    public ResponseEntity<List<Estadia>> getContenedoresListos(@PathVariable Integer idDeposito) {
        return ResponseEntity.ok(estadiaService.findContenedoresListos(idDeposito));
    }

    // 🚚 Registrar entrada de contenedor (llamado desde transporte)
    @PostMapping("/{idDeposito}/entrada/{idContenedor}")
    public ResponseEntity<Estadia> registrarEntrada(
            @PathVariable Integer idDeposito,
            @PathVariable Long idContenedor
    ) {
        return ResponseEntity.ok(estadiaService.registrarEntrada(idDeposito, idContenedor));
    }

    // 🚚 Registrar salida de contenedor (llamado desde transporte)
    @PutMapping("/estadias/{idEstadia}/salida")
    public ResponseEntity<Estadia> registrarSalida(@PathVariable Long idEstadia) {
        return ResponseEntity.ok(estadiaService.registrarSalida(idEstadia));
    }

    // 💰 Calcular costo de estadía (para servicio-tarifa)
    @GetMapping("/{idDeposito}/estadias/costo")
    public ResponseEntity<Double> calcularCosto(
            @PathVariable Integer idDeposito,
            @RequestParam int dias
    ) {
        return ResponseEntity.ok(estadiaService.calcularCostoEstadia(idDeposito, dias));
    }
}
