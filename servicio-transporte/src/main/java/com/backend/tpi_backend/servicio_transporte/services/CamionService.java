package com.backend.tpi_backend.servicio_transporte.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.backend.tpi_backend.servicio_transporte.model.Camion;
import com.backend.tpi_backend.servicio_transporte.repositories.CamionRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CamionService implements BaseService<Camion, String> {

    private final CamionRepository camionRepository;

    @Override
    public List<Camion> findAll() {
        return camionRepository.findAll();
    }

    @Override
    public Optional<Camion> findById(String dominio) {
        return camionRepository.findById(dominio);
    }

    @Override
    public Camion save(Camion camion) {
        return camionRepository.save(camion);
    }

    @Override
    public Camion update(String dominio, Camion camion) {
        if (camionRepository.existsById(dominio)) {
            camion.setDominio(dominio);
            return camionRepository.save(camion);
        }
        throw new RuntimeException("Camión no encontrado con dominio " + dominio);
    }

    @Override
    public void deleteById(String dominio) {
        camionRepository.deleteById(dominio);
    }

    // Lista los camiones disponibles
    @Transactional(readOnly = true) // 'readOnly = true' optimiza las consultas
    public List<Camion> findDisponibles() {
        // Llama al método de CamionRepository
        return camionRepository.findByDisponibilidadTrue();
    }

    public List<Camion> obtenerCamionesElegibles(double peso, double volumen) {
    return camionRepository.findAll()
            .stream()
            .filter(c -> c.getDisponibilidad())
            .filter(c -> c.getCapacidadPesoKg() >= peso)
            .filter(c -> c.getCapacidadVolumenM3() >= volumen)
            .toList();
}
}