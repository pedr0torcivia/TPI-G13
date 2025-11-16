package com.backend.tpi_backend.servicio_deposito.repositories;

import com.backend.tpi_backend.servicio_deposito.model.Deposito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Asume que Deposito tiene Integer como PK
public interface DepositoRepository extends JpaRepository<Deposito, Integer> {
    
    // Método necesario para obtener el Deposito que está asociado a una Ubicacion (ID)
    Optional<Deposito> findByUbicacion_Id(Integer ubicacionId);
}