package com.backend.tpi_backend.servicio_transporte.repositories;

import com.backend.tpi_backend.servicio_transporte.model.TramoTipo;
import java.util.Optional;

public interface TramoTipoRepository extends BaseRepository<TramoTipo, Integer> {
    Optional<TramoTipo> findByNombre(String nombre);
}
