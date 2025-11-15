package com.backend.tpi_backend.servicio_contenedores.repositories;

import com.backend.tpi_backend.servicio_contenedores.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    /**
     * Busca un cliente por su ID de Keycloak (que viene en el JWT).
     * Esto nos permite "enlazar" el usuario de seguridad con el usuario
     * de nuestra base de datos.
     */
    Optional<Cliente> findByIdKeycloak(String idKeycloak);
}