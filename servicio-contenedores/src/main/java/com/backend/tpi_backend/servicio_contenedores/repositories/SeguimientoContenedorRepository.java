package com.backend.tpi_backend.servicio_contenedores.repositories;

import com.backend.tpi_backend.servicio_contenedores.model.Contenedor;
import com.backend.tpi_backend.servicio_contenedores.model.SeguimientoContenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeguimientoContenedorRepository extends JpaRepository<SeguimientoContenedor, Integer> {
    
    // MÉTODO CORREGIDO: Se cambia a OrderByFechaAsc para coincidir con la Entidad.
    List<SeguimientoContenedor> findByContenedor_IdentificacionOrderByFechaAsc(Integer contenedorId);
    SeguimientoContenedor findTopByContenedorOrderByFechaDesc(Contenedor contenedor);


}