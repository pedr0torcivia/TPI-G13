package com.backend.tpi_backend.servicio_contenedores.repositories;

import com.backend.tpi_backend.servicio_contenedores.model.Contenedor;
import com.backend.tpi_backend.servicio_contenedores.model.SeguimientoContenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeguimientoContenedorRepository extends JpaRepository<SeguimientoContenedor, Integer> {

    // Historial de un contenedor, del más nuevo al más viejo
    List<SeguimientoContenedor> findByContenedor_IdentificacionOrderByFechaDesc(Integer contenedorId);

    // Último seguimiento de un contenedor (más reciente)
    SeguimientoContenedor findTopByContenedorOrderByFechaDesc(Contenedor contenedor);
}
