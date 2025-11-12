package com.backend.tpi_backend.servicio_transporte.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.backend.tpi_backend.servicio_transporte.client.ContenedoresClient; 
import org.springframework.stereotype.Service;

import com.backend.tpi_backend.servicio_transporte.model.Camion;
import com.backend.tpi_backend.servicio_transporte.model.Tramo;
import com.backend.tpi_backend.servicio_transporte.model.TramoEstado;
import com.backend.tpi_backend.servicio_transporte.repositories.TramoRepository;

import jakarta.transaction.Transactional;

import com.backend.tpi_backend.servicio_transporte.repositories.CamionRepository;
import com.backend.tpi_backend.servicio_transporte.repositories.TramoEstadoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramoService implements BaseService<Tramo, Integer> {

    private final TramoRepository tramoRepository;
    private final CamionRepository camionRepository;
    private final TramoEstadoRepository tramoEstadoRepository;

    // --- 2. INYECTAR CLIENTE FEIGN ---
    private final ContenedoresClient contenedoresClient;

    // --- 3. IDs de ESTADO del servicio-contenedores ---
    private static final int ID_CONTENEDOR_EN_TRANSITO = 3;
    private static final int ID_CONTENEDOR_ENTREGADO = 4;

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
        // ... (lógica de asignarCamion sin cambios)
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con id " + idTramo));
        Camion camion = camionRepository.findById(dominioCamion)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado con dominio " + dominioCamion));
        if (Boolean.FALSE.equals(camion.getDisponibilidad())) {
            throw new RuntimeException("El camión " + dominioCamion + " no está disponible.");
        }
        TramoEstado estadoAsignado = tramoEstadoRepository.findByNombre("asignado")
                .orElseThrow(() -> new RuntimeException("Estado 'asignado' no encontrado"));
        tramo.setCamion(camion);
        tramo.setEstado(estadoAsignado);
        camion.setDisponibilidad(false);
        tramoRepository.save(tramo);
        camionRepository.save(camion);
        return tramo;
    }

    @Transactional
    public Tramo iniciarTramo(Integer idTramo) {
        // 1. Lógica local
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con id " + idTramo));
        TramoEstado estadoIniciado = tramoEstadoRepository.findByNombre("iniciado")
                .orElseThrow(() -> new RuntimeException("Estado 'iniciado' no encontrado"));
        if (tramo.getCamion() == null) {
            throw new RuntimeException("El tramo no tiene un camión asignado. No puede iniciarse.");
        }
        tramo.setEstado(estadoIniciado);
        tramo.setFechaHoraInicio(LocalDateTime.now());
        Tramo tramoGuardado = tramoRepository.save(tramo);

        // --- 4. LLAMADA FEIGN A SERVICIO-CONTENEDORES (LÓGICA CORREGIDA) ---
        try {
            // PASO 4.1: Obtener ID de solicitud
            Integer solicitudId = tramo.getRuta().getSolicitudId();
            if (solicitudId == null) {
                throw new RuntimeException("El tramo no tiene una solicitud asociada.");
            }

            // PASO 4.2: Llamar a Feign para obtener el ID del contenedor
            Integer idContenedor = contenedoresClient.getContenedorIdBySolicitudId(solicitudId);
            
            Integer idUbicacion = tramo.getOrigenId().intValue(); 

            // PASO 4.3: Actualizar estado en servicio-contenedores
            contenedoresClient.updateEstado(idContenedor, ID_CONTENEDOR_EN_TRANSITO, idUbicacion);

        } catch (Exception e) {
            System.err.println("Error al actualizar estado en Contenedores (iniciarTramo): " + e.getMessage());
            // Opcional: Podríamos revertir el estado local si la llamada Feign es crítica
        }

        return tramoGuardado;
    }

    @Transactional
    public Tramo finalizarTramo(Integer idTramo) {
        // 1. Lógica local
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con id " + idTramo));
        TramoEstado estadoFinalizado = tramoEstadoRepository.findByNombre("finalizado")
                .orElseThrow(() -> new RuntimeException("Estado 'finalizado' no encontrado"));
        if (tramo.getCamion() == null) {
            throw new RuntimeException("El tramo no tiene un camión asignado. No puede finalizarse.");
        }
        tramo.setEstado(estadoFinalizado);
        tramo.setFechaHoraFin(LocalDateTime.now());
        Camion camion = tramo.getCamion();
        camion.setDisponibilidad(true);
        camionRepository.save(camion);
        Tramo tramoGuardado = tramoRepository.save(tramo);

        // --- 5. LLAMADA FEIGN A SERVICIO-CONTENEDORES (LÓGICA MEJORADA) ---
        
        // PASO 5.1: Verificar si este es el último tramo de la ruta
        boolean esUltimoTramo = true;
        List<Tramo> tramosDeLaRuta = tramoRepository.findByRuta(tramo.getRuta());
        
        for (Tramo t : tramosDeLaRuta) {
            // Si encontramos CUALQUIER otro tramo que no esté "finalizado",
            // significa que este NO es el último.
            if (!t.getId().equals(tramoGuardado.getId()) && !t.getEstado().getNombre().equals("finalizado")) {
                esUltimoTramo = false;
                break;
            }
        }

        // PASO 5.2: Decidir el estado a enviar
        Integer estadoContenedorAEnviar = esUltimoTramo ? ID_CONTENEDOR_ENTREGADO : ID_CONTENEDOR_EN_TRANSITO;
        
        try {
            // PASO 5.3: Obtener ID de contenedor
            Integer solicitudId = tramo.getRuta().getSolicitudId();
            Integer idContenedor = contenedoresClient.getContenedorIdBySolicitudId(solicitudId);
            Integer idUbicacion = tramo.getDestinoId().intValue(); 

            // PASO 5.4: Enviar el estado correcto
            contenedoresClient.updateEstado(idContenedor, estadoContenedorAEnviar, idUbicacion);

        } catch (Exception e) {
            System.err.println("Error al actualizar estado en Contenedores (finalizarTramo): " + e.getMessage());
        }

        return tramoGuardado;
    }
}