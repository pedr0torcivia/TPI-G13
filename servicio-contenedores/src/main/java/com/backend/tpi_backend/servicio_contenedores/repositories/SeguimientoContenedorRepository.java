package com.backend.tpi_backend.servicio_contenedores.repositories;

import com.backend.tpi_backend.servicio_contenedores.model.Contenedor;
import com.backend.tpi_backend.servicio_contenedores.model.SeguimientoContenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeguimientoContenedorRepository extends JpaRepository<SeguimientoContenedor, Integer> {
    
    // Método útil para buscar el historial de un contenedor
    List<SeguimientoContenedor> findByContenedor_IdentificacionOrderByFechaHoraAsc(Integer contenedorId);
    SeguimientoContenedor findTopByContenedorOrderByFechaDesc(Contenedor contenedor);


}