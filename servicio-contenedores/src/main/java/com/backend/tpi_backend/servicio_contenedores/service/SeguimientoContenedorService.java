package com.backend.tpi_backend.servicio_contenedores.service;

import com.backend.tpi_backend.servicio_contenedores.model.SeguimientoContenedor;
import com.backend.tpi_backend.servicio_contenedores.repositories.SeguimientoContenedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeguimientoContenedorService {

    private final SeguimientoContenedorRepository repository;

    // Busca todos los seguimientos de un contenedor específico
    public List<SeguimientoContenedor> findAllByContenedorId(Integer contenedorId) {
        return repository.findByContenedor_IdentificacionOrderByFechaHoraAsc(contenedorId);
    }
}