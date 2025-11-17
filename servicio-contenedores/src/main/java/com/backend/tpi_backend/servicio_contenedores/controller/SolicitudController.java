package com.backend.tpi_backend.servicio_contenedores.controller;

import com.backend.tpi_backend.servicio_contenedores.dto.SolicitudRequestDTO;
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


    @GetMapping
    public ResponseEntity<List<Solicitud>> findAll(@RequestParam(required = false) String estado) {
        List<Solicitud> solicitudes;
        if (estado != null && !estado.isEmpty()) {
            solicitudes = service.findByEstadoNombre(estado);
        } else {
            solicitudes = service.findAll();
        }
        return ResponseEntity.ok(solicitudes);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> findById(@PathVariable Integer id) {
        // ✅ Manejamos la excepción lanzada por el Service si la Solicitud no existe
        try {
            Solicitud solicitud = service.findById(id);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            // Devuelve 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/{id}/contenedorId")
    public ResponseEntity<Integer> getContenedorIdBySolicitudId(@PathVariable Integer id) {
        // Manejamos la excepción si la Solicitud no existe o no tiene contenedor
        try {
            return ResponseEntity.ok(service.findContenedorIdBySolicitudId(id));
        } catch (RuntimeException e) {
             return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')") // Solo CLIENTE puede crear solicitudes
    public ResponseEntity<Solicitud> save(
            @RequestBody SolicitudRequestDTO solicitudDTO,
            @AuthenticationPrincipal Jwt jwt) {

        Solicitud guardada = service.save(solicitudDTO, jwt);
        return ResponseEntity.status(201).body(guardada);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')") // Solo OPERADOR puede modificar solicitudes
    public ResponseEntity<Solicitud> update(
            @PathVariable Integer id,
            @RequestBody Solicitud solicitud) {

        return ResponseEntity.ok(service.update(id, solicitud));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')") // Solo OPERADOR puede eliminar solicitudes
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    //Nuevo endpoint para asignar ruta a una solicitud PASO 4
    @PutMapping("/{idSolicitud}/ruta/{idRuta}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Solicitud> asignarRuta(
        @PathVariable Integer idSolicitud,
        @PathVariable Integer idRuta) {

    return ResponseEntity.ok(service.asignarRuta(idSolicitud, idRuta));
    }

}
