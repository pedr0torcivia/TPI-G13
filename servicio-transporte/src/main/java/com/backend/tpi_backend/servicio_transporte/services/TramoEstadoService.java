package com.backend.tpi_backend.servicio_transporte.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.tpi_backend.servicio_transporte.model.TramoEstado;
import com.backend.tpi_backend.servicio_transporte.repositories.TramoEstadoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramoEstadoService implements BaseService<TramoEstado, Integer> {

    private final TramoEstadoRepository tramoEstadoRepository;

    @Override
    public List<TramoEstado> findAll() {
        return tramoEstadoRepository.findAll();
    }

    @Override
    public Optional<TramoEstado> findById(Integer id) {
        return tramoEstadoRepository.findById(id);
    }

    @Override
    public TramoEstado save(TramoEstado tramoEstado) {
        return tramoEstadoRepository.save(tramoEstado);
    }

    @Override
    public TramoEstado update(Integer id, TramoEstado tramoEstado) {
        if (tramoEstadoRepository.existsById(id)) {
            tramoEstado.setId(id);
            return tramoEstadoRepository.save(tramoEstado);
        }
        throw new RuntimeException("TramoEstado no encontrado con id " + id);
    }

    @Override
    public void deleteById(Integer id) {
        tramoEstadoRepository.deleteById(id);
    }
}
