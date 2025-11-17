package com.backend.tpi_backend.servicio_contenedores.controller;

import com.backend.tpi_backend.servicio_contenedores.dto.SolicitudRequestDTO;
import com.backend.tpi_backend.servicio_contenedores.dto.SolicitudResponseDTO;
import com.backend.tpi_backend.servicio_contenedores.model.Solicitud;
import com.backend.tpi_backend.servicio_contenedores.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService service;

    // ==========================
    // LISTAR SOLICITUDES
    // ==========================
    @GetMapping
    public ResponseEntity<List<SolicitudResponseDTO>> findAll(
            @RequestParam(required = false) String estado) {

        return ResponseEntity.ok(service.findAllDto(estado));
    }

    // ==========================
    // OBTENER POR ID (USADO POR FEIGN EN TRANSPORTE)
    // ==========================
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponseDTO> findById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.findDtoById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================
    // OBTENER SOLO ID DEL CONTENEDOR
    // ==========================
    @GetMapping("/{id}/contenedorId")
    public ResponseEntity<Integer> getContenedorIdBySolicitudId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.findContenedorIdBySolicitudId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================
    // CREAR SOLICITUD (R2)
    // ==========================
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')") // Solo CLIENTE puede crear solicitudes
    public ResponseEntity<Solicitud> save(
            @RequestBody SolicitudRequestDTO solicitudDTO,
            @AuthenticationPrincipal Jwt jwt) {

        Solicitud guardada = service.save(solicitudDTO, jwt);
        return ResponseEntity.status(201).body(guardada);
    }

    // ==========================
    // MODIFICAR SOLICITUD
    // ==========================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')") // Solo OPERADOR puede modificar solicitudes
    public ResponseEntity<Solicitud> update(
            @PathVariable Integer id,
            @RequestBody Solicitud solicitud) {

        return ResponseEntity.ok(service.update(id, solicitud));
    }

    // ==========================
    // ELIMINAR SOLICITUD
    // ==========================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')") // Solo OPERADOR puede eliminar solicitudes
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================
    // PASO 4 - ASIGNAR RUTA A SOLICITUD
    // ==========================
    @PutMapping("/{idSolicitud}/ruta/{idRuta}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<SolicitudResponseDTO> asignarRuta(
            @PathVariable Integer idSolicitud,
            @PathVariable Integer idRuta) {

        return ResponseEntity.ok(service.asignarRuta(idSolicitud, idRuta));
    }

}
