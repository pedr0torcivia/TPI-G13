package com.backend.tpi_backend.servicio_transporte.repositories;

import java.util.List;

import com.backend.tpi_backend.servicio_transporte.model.Ruta;

public interface RutaRepository extends BaseRepository<Ruta, Integer> {
    // Filtra las rutas que pertenecen a una solicitud específica
    List<Ruta> findBySolicitudId(Integer solicitudId);
}
