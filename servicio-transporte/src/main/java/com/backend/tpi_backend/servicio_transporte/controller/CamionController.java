package com.backend.tpi_backend.servicio_transporte.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.tpi_backend.servicio_transporte.model.Camion;
import com.backend.tpi_backend.servicio_transporte.services.CamionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/camiones")
@RequiredArgsConstructor
public class CamionController {

    private final CamionService camionService;

    @GetMapping
    public ResponseEntity<List<Camion>> getAll() {
        return ResponseEntity.ok(camionService.findAll());
    }

    @GetMapping("/{dominio}")
    public ResponseEntity<Camion> getById(@PathVariable String dominio) {
        return camionService.findById(dominio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Camion> create(@RequestBody Camion camion) {
        return ResponseEntity.ok(camionService.save(camion));
    }

    @PutMapping("/{dominio}")
    public ResponseEntity<Camion> update(@PathVariable String dominio, @RequestBody Camion camion) {
        return ResponseEntity.ok(camionService.update(dominio, camion));
    }

    @DeleteMapping("/{dominio}")
    public ResponseEntity<Void> delete(@PathVariable String dominio) {
        camionService.deleteById(dominio);
        return ResponseEntity.noContent().build();
    }

    // Para listar camiones disponibles
    @Operation(
        summary = "Obtiene todos los camiones disponibles",
        description = "Devuelve una lista de camiones cuya bandera 'disponibilidad' es VERDADERA."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de camiones disponibles (puede ser vacía).")
    })
    @GetMapping("/disponibles") 
    public ResponseEntity<List<Camion>> getDisponibles() {
        List<Camion> camionesDisponibles = camionService.findDisponibles();
        return ResponseEntity.ok(camionesDisponibles);
    }

    @GetMapping("/elegibles")
    public List<Camion> obtenerCamionesElegibles(
            @RequestParam double peso,
            @RequestParam double volumen) {

        return camionService.obtenerCamionesElegibles(peso, volumen);
    }
}