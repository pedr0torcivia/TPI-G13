package com.backend.tpi_backend.servicio_contenedores.service;

import com.backend.tpi_backend.servicio_contenedores.model.ContenedorEstado;
import com.backend.tpi_backend.servicio_contenedores.repositories.ContenedorEstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContenedorEstadoService {

    private final ContenedorEstadoRepository repository;

    public List<ContenedorEstado> findAll() {
        return repository.findAll();
    }

    public ContenedorEstado findById(Integer id) {
        return repository.findById(id).orElseThrow(
            () -> new RuntimeException("Estado de Contenedor no encontrado con ID: " + id)
        );
    }
}