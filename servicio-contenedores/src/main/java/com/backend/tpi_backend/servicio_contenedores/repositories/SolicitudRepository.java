package com.backend.tpi_backend.servicio_contenedores.repositories;

import com.backend.tpi_backend.servicio_contenedores.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Importar List

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {

    // NUEVO MÉTODO: Busca solicitudes por el nombre de su estado (ej: "programada", "en_transito")
    List<Solicitud> findByEstado_Nombre(String nombreEstado);
}