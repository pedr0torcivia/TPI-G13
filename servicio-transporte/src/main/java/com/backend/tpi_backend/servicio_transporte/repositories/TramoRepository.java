package com.backend.tpi_backend.servicio_transporte.repositories;

import java.util.List;

import com.backend.tpi_backend.servicio_transporte.model.Ruta;
import com.backend.tpi_backend.servicio_transporte.model.Tramo;

public interface TramoRepository extends BaseRepository<Tramo, Integer> {
    List<Tramo> findByRuta(Ruta ruta);
}
