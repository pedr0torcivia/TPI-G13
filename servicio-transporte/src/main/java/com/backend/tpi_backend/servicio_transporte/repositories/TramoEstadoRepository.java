package com.backend.tpi_backend.servicio_transporte.repositories;

import java.util.Optional;

import com.backend.tpi_backend.servicio_transporte.model.TramoEstado;

public interface TramoEstadoRepository extends BaseRepository<TramoEstado, Integer> {
    Optional<TramoEstado> findByNombre(String nombre); // optional para evitar null cuando el metodo no encuentra algo
}