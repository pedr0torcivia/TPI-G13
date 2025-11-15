package com.backend.tpi_backend.servicio_transporte.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.tpi_backend.servicio_transporte.model.Tramo;
import com.backend.tpi_backend.servicio_transporte.model.Transportista;
import com.backend.tpi_backend.servicio_transporte.repositories.TramoRepository;
import com.backend.tpi_backend.servicio_transporte.repositories.TransportistaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransportistaService implements BaseService<Transportista, Integer> {

    private final TransportistaRepository transportistaRepository;
    private final TramoRepository tramoRepository;

    @Override
    public List<Transportista> findAll() {
        return transportistaRepository.findAll();
    }

    @Override
    public Optional<Transportista> findById(Integer id) {
        return transportistaRepository.findById(id);
    }

    @Override
    public Transportista save(Transportista transportista) {
        return transportistaRepository.save(transportista);
    }

    @Override
    public Transportista update(Integer id, Transportista transportista) {
        if (transportistaRepository.existsById(id)) {
            transportista.setId(id);
            return transportistaRepository.save(transportista);
        }
        throw new RuntimeException("Transportista no encontrado con id " + id);
    }

    @Override
    public void deleteById(Integer id) {
        transportistaRepository.deleteById(id);
    }

    // Transportista puede ver sus tramos asignados
    public List<Tramo> obtenerTramosAsignados(Integer idTransportista) {

        // Verificamos que exista el transportista
        if (!transportistaRepository.existsById(idTransportista)) {
            throw new RuntimeException("Transportista no encontrado con id " + idTransportista);
        }

        return tramoRepository.findByCamion_Transportista_Id(idTransportista);
    }
}