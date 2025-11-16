package com.backend.tpi_backend.servicio_transporte.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.backend.tpi_backend.servicio_transporte.dto.RutaAsignacionRequest;
import com.backend.tpi_backend.servicio_transporte.dto.RutaTentativaResponse;
import com.backend.tpi_backend.servicio_transporte.model.Ruta;
import com.backend.tpi_backend.servicio_transporte.services.RutaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaService rutaService;

    @GetMapping
    public ResponseEntity<List<Ruta>> getAll() {
        return ResponseEntity.ok(rutaService.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Ruta> getById(@PathVariable Integer id) {
        return rutaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Ruta> create(@RequestBody Ruta ruta) {
        return ResponseEntity.ok(rutaService.save(ruta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ruta> update(@PathVariable Integer id, @RequestBody Ruta ruta) {
        return ResponseEntity.ok(rutaService.update(id, ruta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        rutaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<Ruta>> obtenerRutasPorSolicitud(@PathVariable Integer idSolicitud) {
        return ResponseEntity.ok(rutaService.obtenerRutasPorSolicitud(idSolicitud));
    }

    // generar ruta tentativa
    @PreAuthorize("hasRole('OPERADOR')")
    @GetMapping("/ruta-tentativa")
    public ResponseEntity<RutaTentativaResponse> generarRutaTentativa(
            @RequestParam Integer origenId,
            @RequestParam Integer destinoId,
            @RequestParam Integer contenedorId) {

        RutaTentativaResponse response = rutaService.generarRutaTentativa(origenId, destinoId, contenedorId);

        return ResponseEntity.ok(response);
    }

    // asignar ruta a solicitud
    @PreAuthorize("hasRole('OPERADOR')")
    @PostMapping("/asignar")
    public ResponseEntity<?> asignarRuta(@RequestBody RutaAsignacionRequest request) {

        Ruta ruta = rutaService.asignarRutaASolicitud(request.getSolicitudId());

        return ResponseEntity.ok(
                Map.of(
                        "mensaje", "Ruta asignada correctamente",
                        "rutaId", ruta.getId()));
    }
}
