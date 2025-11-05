package com.backend.tpi_backend.servicio_contenedores.repositories;

import com.backend.tpi_backend.servicio_contenedores.model.SeguimientoContenedor;
import com.backend.tpi_backend.servicio_contenedores.model.Contenedor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeguimientoContenedorRepository extends BaseRepository<SeguimientoContenedor, Long> {

    // Últimos N puntos de un contenedor (ordenados por fecha_hora desc)
    @Query("""
           SELECT s
           FROM SeguimientoContenedor s
           WHERE s.contenedor = :contenedor
           ORDER BY s.fechaHora DESC
           """)
    List<SeguimientoContenedor> findUltimosByContenedor(Contenedor contenedor, Pageable topN);

    // Timeline completo ordenado ascendente
    List<SeguimientoContenedor> findAllByContenedorOrderByFechaHoraAsc(Contenedor contenedor);
}
