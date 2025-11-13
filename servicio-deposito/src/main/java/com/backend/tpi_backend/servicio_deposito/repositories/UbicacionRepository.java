package com.backend.tpi_backend.servicio_deposito.repositories;

import com.backend.tpi_backend.servicio_deposito.model.Ubicacion;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UbicacionRepository extends BaseRepository<Ubicacion, Integer> {

    // 🔹 Buscar todas las ubicaciones de una ciudad
    List<Ubicacion> findByCiudadId(Integer idCiudad);

    // 🔹 Buscar todas las ubicaciones de una provincia (vía relación Ciudad → Provincia)
    List<Ubicacion> findByCiudadProvinciaId(Integer idProvincia);
}
