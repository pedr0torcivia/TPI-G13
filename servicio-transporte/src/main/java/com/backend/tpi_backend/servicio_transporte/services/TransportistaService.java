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

    // --- MÉTODO MODIFICADO (AHORA ES SEGURO) ---
    /**
     * Devuelve los tramos asignados a un transportista,
     * buscándolo por su ID de Keycloak (el 'sub' del JWT).
     */
    public List<Tramo> obtenerTramosAsignadosPorKeycloakId(String idKeycloak) {

        // Ya no necesitamos verificar si el transportista existe.
        // Si el idKeycloak es inválido, el repositorio simplemente
        // devolverá una lista vacía, lo cual es seguro.

        return tramoRepository.findByCamion_Transportista_IdKeycloak(idKeycloak);
    }
    
    // (Dejamos el método viejo por si lo usa otra parte del sistema,
    // pero el controller ya no lo va a llamar)
    public List<Tramo> obtenerTramosAsignados(Integer idTransportista) {
        if (!transportistaRepository.existsById(idTransportista)) {
            throw new RuntimeException("Transportista no encontrado con id " + idTransportista);
    }
        return tramoRepository.findByCamion_Transportista_Id(idTransportista);
    }
}