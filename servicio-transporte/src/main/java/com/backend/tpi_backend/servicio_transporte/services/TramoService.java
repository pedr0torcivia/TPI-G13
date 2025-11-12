package com.backend.tpi_backend.servicio_transporte.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.tpi_backend.servicio_transporte.model.Camion;
import com.backend.tpi_backend.servicio_transporte.model.Tramo;
import com.backend.tpi_backend.servicio_transporte.model.TramoEstado;
import com.backend.tpi_backend.servicio_transporte.repositories.TramoRepository;
import com.backend.tpi_backend.servicio_transporte.repositories.CamionRepository;
import com.backend.tpi_backend.servicio_transporte.repositories.TramoEstadoRepository;

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

    public Tramo asignarCamion(Integer idTramo, String dominioCamion) {
    // 1. Buscar el tramo por ID
    Tramo tramo = tramoRepository.findById(idTramo)
            .orElseThrow(() -> new RuntimeException("Tramo no encontrado con id " + idTramo));

    // 2. Buscar el camión por dominio
    Camion camion = camionRepository.findById(dominioCamion)
            .orElseThrow(() -> new RuntimeException("Camión no encontrado con dominio " + dominioCamion));

    // 3. Validar disponibilidad del camión
    if (Boolean.FALSE.equals(camion.getDisponibilidad())) {
        throw new RuntimeException("El camión " + dominioCamion + " no está disponible.");
    }

    // 4. Buscar estado "asignado"
    TramoEstado estadoAsignado = tramoEstadoRepository.findByNombre("asignado")
            .orElseThrow(() -> new RuntimeException("Estado 'asignado' no encontrado"));

    // 5. Actualizar el tramo y el camión
    tramo.setCamion(camion);
    tramo.setEstado(estadoAsignado);
    camion.setDisponibilidad(false);

    tramoRepository.save(tramo);
    camionRepository.save(camion);

    return tramo;
    }

    public Tramo iniciarTramo(Integer idTramo) {
    // 1. Buscar el tramo por ID
    Tramo tramo = tramoRepository.findById(idTramo)
            .orElseThrow(() -> new RuntimeException("Tramo no encontrado con id " + idTramo));

    // 2. Buscar estado "iniciado"
    TramoEstado estadoIniciado = tramoEstadoRepository.findByNombre("iniciado")
            .orElseThrow(() -> new RuntimeException("Estado 'iniciado' no encontrado"));

    // 3. Validar que el tramo tenga camión asignado antes de iniciarse
    if (tramo.getCamion() == null) {
        throw new RuntimeException("El tramo no tiene un camión asignado. No puede iniciarse.");
    }

    // 4. Actualizar el estado y la fecha de inicio
    tramo.setEstado(estadoIniciado);
    tramo.setFechaHoraInicio(LocalDateTime.now());

    // 5. Guardar los cambios
    return tramoRepository.save(tramo);
    }

    public Tramo finalizarTramo(Integer idTramo) {
    // 1. Buscar el tramo
    Tramo tramo = tramoRepository.findById(idTramo)
            .orElseThrow(() -> new RuntimeException("Tramo no encontrado con id " + idTramo));

    // 2. Buscar el estado "finalizado"
    TramoEstado estadoFinalizado = tramoEstadoRepository.findByNombre("finalizado")
            .orElseThrow(() -> new RuntimeException("Estado 'finalizado' no encontrado"));

    // 3. Validar que tenga un camión asignado
    if (tramo.getCamion() == null) {
        throw new RuntimeException("El tramo no tiene un camión asignado. No puede finalizarse.");
    }

    // 4. Actualizar estado y fecha
    tramo.setEstado(estadoFinalizado);
    tramo.setFechaHoraFin(LocalDateTime.now());

    // 5. Liberar el camión (volver a disponible)
    Camion camion = tramo.getCamion();
    camion.setDisponibilidad(true);

    // 6. Guardar cambios
    camionRepository.save(camion);
    return tramoRepository.save(tramo);
    }
}