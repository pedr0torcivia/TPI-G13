package com.backend.tpi_backend.servicio_contenedores.repositories;

import com.backend.tpi_backend.servicio_contenedores.model.Solicitud;
import com.backend.tpi_backend.servicio_contenedores.model.Cliente;
import com.backend.tpi_backend.servicio_contenedores.model.Contenedor;
import com.backend.tpi_backend.servicio_contenedores.model.EstadoSolicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudRepository extends BaseRepository<Solicitud, Long> {

    Page<Solicitud> findAllByCliente(Cliente cliente, Pageable pageable);

    Page<Solicitud> findAllByContenedor(Contenedor contenedor, Pageable pageable);

    Page<Solicitud> findAllByEstado(EstadoSolicitud estado, Pageable pageable);
}
