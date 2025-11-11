package com.backend.tpi_backend.servicio_deposito.repositories;

import com.backend.tpi_backend.servicio_deposito.model.Estadia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadiaRepository extends JpaRepository<Estadia, Long> {
    List<Estadia> findByDepositoId(Integer idDeposito);
}
