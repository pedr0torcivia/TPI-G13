package com.backend.tpi_backend.servicio_contenedores.repositories;
import com.backend.tpi_backend.servicio_contenedores.model.SolicitudEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudEstadoRepository extends JpaRepository<SolicitudEstado, Integer> {}