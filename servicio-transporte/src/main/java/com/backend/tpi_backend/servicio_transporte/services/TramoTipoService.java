package com.backend.tpi_backend.servicio_transporte.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.tpi_backend.servicio_transporte.model.TramoTipo;
import com.backend.tpi_backend.servicio_transporte.repositories.TramoTipoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramoTipoService implements BaseService<TramoTipo, Integer> {

    private final TramoTipoRepository tramoTipoRepository;

    @Override
    public List<TramoTipo> findAll() {
        return tramoTipoRepository.findAll();
    }

    @Override
    public Optional<TramoTipo> findById(Integer id) {
        return tramoTipoRepository.findById(id);
    }

    @Override
    public TramoTipo save(TramoTipo tramoTipo) {
        return tramoTipoRepository.save(tramoTipo);
    }

    @Override
    public TramoTipo update(Integer id, TramoTipo tramoTipo) {
        if (tramoTipoRepository.existsById(id)) {
            tramoTipo.setId(id);
            return tramoTipoRepository.save(tramoTipo);
        }
        throw new RuntimeException("TramoTipo no encontrado con id " + id);
    }

    @Override
    public void deleteById(Integer id) {
        tramoTipoRepository.deleteById(id);
    }
}