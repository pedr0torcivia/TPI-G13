package com.backend.tpi_backend.servicio_transporte.repositories;

import java.util.List;

import com.backend.tpi_backend.servicio_transporte.model.Ruta;
import com.backend.tpi_backend.servicio_transporte.model.Tramo;

public interface TramoRepository extends BaseRepository<Tramo, Integer> {
    List<Tramo> findByRuta(Ruta ruta);
    
    // Método viejo (ya no lo usaremos para esto)
    List<Tramo> findByCamion_Transportista_Id(Integer idTransportista);

    // --- ¡MÉTODO NUEVO Y SEGURO! ---
    // Busca tramos anidando por Camion -> Transportista -> IdKeycloak
    List<Tramo> findByCamion_Transportista_IdKeycloak(String idKeycloak);
}