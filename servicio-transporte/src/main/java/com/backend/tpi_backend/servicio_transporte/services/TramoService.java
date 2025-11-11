package com.backend.tpi_backend.servicio_transporte.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.tpi_backend.servicio_transporte.model.Camion;
import com.backend.tpi_backend.servicio_transporte.model.Tramo;
import com.backend.tpi_backend.servicio_transporte.model.TramoEstado;
import com.backend.tpi_backend.servicio_transporte.repositories.CamionRepository;
import com.backend.tpi_backend.servicio_transporte.repositories.TramoEstadoRepository;
import com.backend.tpi_backend.servicio_transporte.repositories.TramoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramoService implements BaseService<Tramo, Integer> {

    private final TramoRepository tramoRepository;
    private final CamionRepository camionRepository;
    private final TramoEstadoRepository tramoEstadoRepository;

    @Override
    public List<Tramo> findAll() {
        return tramoRepository.findAll();
    }

    @Override
    public Optional<Tramo> findById(Integer id) {
        return tramoRepository.findById(id);
    }

    @Override
    public Tramo save(Tramo tramo) {
        return tramoRepository.save(tramo);
    }

    @Override
    public Tramo update(Integer id, Tramo tramo) {
        if (tramoRepository.existsById(id)) {
            tramo.setId(id);
            return tramoRepository.save(tramo);
        }
        throw new RuntimeException("Tramo no encontrado con id " + id);
    }

    @Override
    public void deleteById(Integer id) {
        tramoRepository.deleteById(id);
    }

}