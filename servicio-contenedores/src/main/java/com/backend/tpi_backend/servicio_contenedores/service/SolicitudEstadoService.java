package com.backend.tpi_backend.servicio_contenedores.service;

import com.backend.tpi_backend.servicio_contenedores.model.SolicitudEstado;
import com.backend.tpi_backend.servicio_contenedores.repositories.SolicitudEstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudEstadoService {

    private final SolicitudEstadoRepository repository;

    public List<SolicitudEstado> findAll() {
        return repository.findAll();
    }

    public SolicitudEstado findById(Integer id) {
        return repository.findById(id).orElseThrow(
            () -> new RuntimeException("Estado de Solicitud no encontrado con ID: " + id)
        );
    }
}