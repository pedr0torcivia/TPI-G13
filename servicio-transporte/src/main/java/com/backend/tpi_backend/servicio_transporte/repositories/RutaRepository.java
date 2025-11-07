package com.backend.tpi_backend.servicio_transporte.repositories;

import java.util.List;

import com.backend.tpi_backend.servicio_transporte.model.Ruta;

public interface RutaRepository extends BaseRepository<Ruta, Long> {
    List<Ruta> findBySolicitudId(Long solicitudId);
}
