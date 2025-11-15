package com.backend.tpi_backend.servicio_contenedores.controller;

import com.backend.tpi_backend.servicio_contenedores.dto.ContenedorDTO; // <-- IMPORTAR DTO
import com.backend.tpi_backend.servicio_contenedores.dto.ContenedorPendienteDTO;
import com.backend.tpi_backend.servicio_contenedores.dto.EstadoContenedorResponse;
import com.backend.tpi_backend.servicio_contenedores.model.Contenedor;
import com.backend.tpi_backend.servicio_contenedores.model.ContenedorEstado;
import com.backend.tpi_backend.servicio_contenedores.service.ContenedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenedores")
@RequiredArgsConstructor
public class ContenedorController {

    private final ContenedorService service;

    // --- MODIFICADO: Usa service.findAllDTO() ---
    @GetMapping
    public ResponseEntity<List<ContenedorDTO>> findAll() {
        return ResponseEntity.ok(service.findAllDTO());
    }

    // --- MODIFICADO: Usa service.findDTOById() ---
    // Este es el endpoint que estaba fallando
    @GetMapping("/{id}")
    public ResponseEntity<ContenedorDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findDTOById(id));
    }

    @GetMapping("/{id}/estado")
    public ResponseEntity<ContenedorEstado> getEstadoById(@PathVariable Integer id) {
        ContenedorEstado estado = service.findEstadoById(id);
        return ResponseEntity.ok(estado);
    }

    // --- MODIFICADO: Usa service.updateEstado() ---
    @PutMapping("/{id}/estado")
    public ResponseEntity<ContenedorDTO> updateEstado(
            @PathVariable Integer id,
            @RequestParam Integer estadoId,
            @RequestParam Integer ubicacionId) {
        
        ContenedorDTO actualizado = service.updateEstado(id, estadoId, ubicacionId);
        return ResponseEntity.ok(actualizado);
    }

    // --- MODIFICADO: Usa service.save() ---
    @PostMapping
    public ResponseEntity<ContenedorDTO> save(@RequestBody Contenedor contenedor,
                                              @RequestParam Integer clienteId,
                                              @RequestParam Integer ubicacionId) {
        ContenedorDTO guardado = service.save(contenedor, clienteId, ubicacionId);
        return ResponseEntity.status(201).body(guardado);
    }

    // --- MODIFICADO: Usa service.update() ---
    @PutMapping("/{id}")
    public ResponseEntity<ContenedorDTO> update(@PathVariable Integer id, @RequestBody Contenedor contenedor) {
        return ResponseEntity.ok(service.update(id, contenedor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/estado-transporte")
    public ResponseEntity<EstadoContenedorResponse> consultarEstadoTransporte(
        @PathVariable Integer id) {
    return ResponseEntity.ok(service.consultarEstadoTransporte(id));
    }
    //Funcionalidad 5 - Listar contenedores no disponibles
    @GetMapping("/pendientes")
public ResponseEntity<List<ContenedorPendienteDTO>> getPendientes(
        @RequestParam(required = false) Integer estadoId,
        @RequestParam(required = false) Integer clienteId,
        @RequestParam(required = false) Integer ubicacionId
) {
    return ResponseEntity.ok(
        service.obtenerPendientes(estadoId, clienteId, ubicacionId)
    );
}

}