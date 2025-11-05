package com.backend.tpi_backend.servicio_contenedores.repositories;

import com.backend.tpi_backend.servicio_contenedores.model.Contenedor;
import com.backend.tpi_backend.servicio_contenedores.model.EstadoContenedor;
import com.backend.tpi_backend.servicio_contenedores.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContenedorRepository extends BaseRepository<Contenedor, Long> {

    Page<Contenedor> findAllByCliente(Cliente cliente, Pageable pageable);

    Page<Contenedor> findAllByEstado(EstadoContenedor estado, Pageable pageable);

    List<Contenedor> findTop10ByEstadoOrderByIdentificacionDesc(EstadoContenedor estado);
}
