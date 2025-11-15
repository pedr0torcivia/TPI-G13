package com.backend.tpi_backend.servicio_transporte.repositories;

import java.util.List;

import com.backend.tpi_backend.servicio_transporte.model.Ruta;
import com.backend.tpi_backend.servicio_transporte.model.Tramo;

public interface TramoRepository extends BaseRepository<Tramo, Integer> {
    List<Tramo> findByRuta(Ruta ruta);
    // Nuevo método: Tramos por TRANSPORTISTA (a través del camión)
    List<Tramo> findByCamion_Transportista_Id(Integer idTransportista);
}
