package com.backend.tpi_backend.servicio_deposito.services;

import com.backend.tpi_backend.servicio_deposito.model.Provincia;
import com.backend.tpi_backend.servicio_deposito.repositories.ProvinciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProvinciaService implements BaseService<Provincia, Integer> {

    private final ProvinciaRepository provinciaRepository;

    @Override
    public List<Provincia> findAll() {
        return provinciaRepository.findAll();
    }

    @Override
    public Optional<Provincia> findById(Integer id) {
        return provinciaRepository.findById(id);
    }

    @Override
    public Provincia save(Provincia provincia) {
        return provinciaRepository.save(provincia);
    }

    @Override
    public Provincia update(Integer id, Provincia provincia) {
        if (provinciaRepository.existsById(id)) {
            provincia.setId(id);
            return provinciaRepository.save(provincia);
        }
        throw new RuntimeException("Provincia no encontrada con id " + id);
    }

    @Override
    public void deleteById(Integer id) {
        provinciaRepository.deleteById(id);
    }
}
