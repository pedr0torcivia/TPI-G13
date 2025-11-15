package com.backend.tpi_backend.servicio_contenedores.repositories;

import com.backend.tpi_backend.servicio_contenedores.model.Contenedor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContenedorRepository extends JpaRepository<Contenedor, Integer> {
    List<Contenedor> findByEstado_IdNot(Integer estadoId);
}