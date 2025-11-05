package com.backend.tpi_backend.servicio_contenedores.repositories;

import com.backend.tpi_backend.servicio_contenedores.model.EstadoSolicitud;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoSolicitudRepository extends BaseRepository<EstadoSolicitud, Long> {
    Optional<EstadoSolicitud> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
