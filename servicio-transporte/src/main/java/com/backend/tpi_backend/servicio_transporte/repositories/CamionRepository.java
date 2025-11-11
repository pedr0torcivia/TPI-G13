package com.backend.tpi_backend.servicio_transporte.repositories;

import java.util.List;

import com.backend.tpi_backend.servicio_transporte.model.Camion;

public interface CamionRepository extends BaseRepository<Camion, String> {
    List<Camion> findByDisponibilidadTrue();
}
