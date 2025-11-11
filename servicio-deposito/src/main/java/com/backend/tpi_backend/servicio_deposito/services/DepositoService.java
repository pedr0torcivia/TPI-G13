package com.backend.tpi_backend.servicio_deposito.services;

import com.backend.tpi_backend.servicio_deposito.model.Deposito;
import com.backend.tpi_backend.servicio_deposito.repositories.DepositoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepositoService implements BaseService<Deposito, Integer> {

    private final DepositoRepository depositoRepository;

    @Override
    public List<Deposito> findAll() {
        return depositoRepository.findAll();
    }

    @Override
    public Optional<Deposito> findById(Integer id) {
        return depositoRepository.findById(id);
    }

    @Override
    public Deposito save(Deposito deposito) {
        return depositoRepository.save(deposito);
    }

    @Override
    public Deposito update(Integer id, Deposito deposito) {
        if (depositoRepository.existsById(id)) {
            deposito.setId(id);
            return depositoRepository.save(deposito);
        }
        throw new RuntimeException("Depósito no encontrado con id " + id);
    }

    @Override
    public void deleteById(Integer id) {
        depositoRepository.deleteById(id);
    }
}
