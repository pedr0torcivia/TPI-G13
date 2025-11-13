package com.backend.tpi_backend.servicio_deposito.services;

import com.backend.tpi_backend.servicio_deposito.model.Ubicacion;
import com.backend.tpi_backend.servicio_deposito.repositories.UbicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UbicacionService implements BaseService<Ubicacion, Integer> {

    private final UbicacionRepository ubicacionRepository;

    @Override
    public List<Ubicacion> findAll() {
        return ubicacionRepository.findAll();
    }

    @Override
    public Optional<Ubicacion> findById(Integer id) {
        return ubicacionRepository.findById(id);
    }

    @Override
    public Ubicacion save(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    @Override
    public Ubicacion update(Integer id, Ubicacion ubicacion) {
        if (ubicacionRepository.existsById(id)) {
            ubicacion.setId(id);
            return ubicacionRepository.save(ubicacion);
        }
        throw new RuntimeException("Ubicación no encontrada con id " + id);
    }

    @Override
    public void deleteById(Integer id) {
        ubicacionRepository.deleteById(id);
    }

    // ✅ Nuevos métodos de búsqueda

    public List<Ubicacion> buscarPorCiudad(Integer idCiudad) {
        return ubicacionRepository.findByCiudadId(idCiudad);
    }

    public List<Ubicacion> buscarPorProvincia(Integer idProvincia) {
        return ubicacionRepository.findByCiudadProvinciaId(idProvincia);
    }
}
