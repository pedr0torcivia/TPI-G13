package com.backend.tpi_backend.servicio_transporte.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.tpi_backend.servicio_transporte.model.Ruta;
import com.backend.tpi_backend.servicio_transporte.repositories.RutaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RutaService implements BaseService<Ruta, Integer> {

    private final RutaRepository rutaRepository;

    @Override
    public List<Ruta> findAll() {
        return rutaRepository.findAll();
    }

    @Override
    public Optional<Ruta> findById(Integer id) {
        return rutaRepository.findById(id);
    }

    @Override
    public Ruta save(Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    @Override
    public Ruta update(Integer id, Ruta ruta) {
        if (rutaRepository.existsById(id)) {
            ruta.setId(id);
            return rutaRepository.save(ruta);
        }
        throw new RuntimeException("Ruta no encontrada con id " + id);
    }

    @Override
    public void deleteById(Integer id) {
        rutaRepository.deleteById(id);
    }

    public List<Ruta> obtenerRutasPorSolicitud(Integer idSolicitud) {
        return rutaRepository.findBySolicitudId(idSolicitud);
    }
}
