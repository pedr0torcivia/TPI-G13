package com.backend.tpi_backend.servicio_transporte.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // <-- IMPORTAR
import org.springframework.security.oauth2.jwt.Jwt; // <-- IMPORTAR
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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transportistas")
@RequiredArgsConstructor
public class TransportistaController {

    private final TransportistaService transportistaService;

    // --- ENDPOINTS DE OPERADOR (Estos quedan igual) ---
    @GetMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<Transportista>> getAll() {
        return ResponseEntity.ok(transportistaService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Transportista> getById(@PathVariable Integer id) {
        return transportistaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Transportista> create(@RequestBody Transportista transportista) {
        return ResponseEntity.ok(transportistaService.save(transportista));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Transportista> update(@PathVariable Integer id, @RequestBody Transportista transportista) {
        return ResponseEntity.ok(transportistaService.update(id, transportista));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        transportistaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- ENDPOINT DE TRANSPORTISTA (¡AHORA ES 100% SEGURO!) ---
    
    /**
     * Devuelve solo los tramos asignados al TRANSPORTISTA que está
     * actualmente logueado (basado en su Token JWT).
     * Un transportista no puede espiar los tramos de otro.
     */
    @GetMapping("/mis-tramos") // <-- CAMBIAMOS LA URL (ya no pide ID)
    @PreAuthorize("hasRole('TRANSPORTISTA')")
    public ResponseEntity<List<Tramo>> getMisTramos(@AuthenticationPrincipal Jwt jwt) {
        
        // 1. Obtenemos el ID de Keycloak (el 'sub') del token.
        String idKeycloak = jwt.getSubject();

        // 2. Llamamos al nuevo método del servicio.
        List<Tramo> tramos = transportistaService.obtenerTramosAsignadosPorKeycloakId(idKeycloak);
        
        return ResponseEntity.ok(tramos);
    }
}