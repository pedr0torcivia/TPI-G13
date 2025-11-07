package com.backend.tpi_backend.servicio_deposito.services;

import com.backend.tpi_backend.servicio_deposito.model.Ciudad;
import com.backend.tpi_backend.servicio_deposito.repositories.CiudadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CiudadService implements BaseService<Ciudad, Integer> {

    private final CiudadRepository ciudadRepository;

    @Override
    public List<Ciudad> findAll() {
        return ciudadRepository.findAll();
    }

    @Override
    public Optional<Ciudad> findById(Integer id) {
        return ciudadRepository.findById(id);
    }

    @Override
    public Ciudad save(Ciudad ciudad) {
        return ciudadRepository.save(ciudad);
    }

    @Override
    public Ciudad update(Integer id, Ciudad ciudad) {
        if (ciudadRepository.existsById(id)) {
            ciudad.setId(id);
            return ciudadRepository.save(ciudad);
        }
        throw new RuntimeException("Ciudad no encontrada con id " + id);
    }

    @Override
    public void deleteById(Integer id) {
        ciudadRepository.deleteById(id);
    }
}
