package com.backend.tpi_backend.servicio_contenedores.service;

import com.backend.tpi_backend.servicio_contenedores.model.*;
import com.backend.tpi_backend.servicio_contenedores.repositories.ContenedorRepository;
import com.backend.tpi_backend.servicio_contenedores.repositories.SeguimientoContenedorRepository;
import com.backend.tpi_backend.servicio_contenedores.repositories.SolicitudRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    // IDs de estado fijos
    private static final int ID_SOLICITUD_BORRADOR = 1;
    private static final int ID_CONTENEDOR_DISPONIBLE = 1;
    private static final int ID_CONTENEDOR_ASIGNADO = 2;

    private final SolicitudRepository solicitudRepository;
    private final ContenedorService contenedorService;
    private final ContenedorRepository contenedorRepository;
    private final SolicitudEstadoService solicitudEstadoService;
    private final ContenedorEstadoService contenedorEstadoService;
    private final SeguimientoContenedorRepository seguimientoRepository;

    public List<Solicitud> findAll() {
        return solicitudRepository.findAll();
    }

    public Solicitud findById(Integer id) {
        return solicitudRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Solicitud no encontrada con ID: " + id)
        );
    }

    @Transactional
    public Solicitud save(Solicitud solicitud, Integer contenedorId) {
        // 1. Buscar entidades principales
        Contenedor contenedor = contenedorService.findById(contenedorId);
        
        if (contenedor.getEstado().getId() != ID_CONTENEDOR_DISPONIBLE) {
             throw new RuntimeException("El contenedor " + contenedorId + " no está disponible.");
        }
        
        Cliente cliente = contenedor.getCliente();
        SolicitudEstado estadoBorrador = solicitudEstadoService.findById(ID_SOLICITUD_BORRADOR);

        // 2. Asignar relaciones al objeto solicitud
        solicitud.setContenedor(contenedor);
        solicitud.setCliente(cliente);
        solicitud.setEstado(estadoBorrador);
        // Los IDs (origenId, destinoId, tarifaId) deben venir en el objeto 'solicitud'

        // 3. Guardar la solicitud
        Solicitud solicitudGuardada = solicitudRepository.save(solicitud);

        // 4. Actualizar el estado del contenedor a "asignado"
        ContenedorEstado estadoAsignado = contenedorEstadoService.findById(ID_CONTENEDOR_ASIGNADO);
        contenedor.setEstado(estadoAsignado);
        contenedorRepository.save(contenedor);

        // 5. Crear seguimiento para el contenedor
        SeguimientoContenedor seguimiento = new SeguimientoContenedor(
            contenedor,
            estadoAsignado,
            solicitud.getOrigenId() // La ubicación ahora es el origen de la solicitud
        );
        seguimientoRepository.save(seguimiento);

        return solicitudGuardada;
    }

    @Transactional
    public Solicitud update(Integer id, Solicitud solicitudActualizada) {
        Solicitud solicitudExistente = findById(id);

        if (solicitudExistente.getEstado().getId() != ID_SOLICITUD_BORRADOR) {
            throw new RuntimeException("Solo se pueden modificar solicitudes en estado 'borrador'.");
        }
        
        // No se puede cambiar el contenedor, pero sí el resto
        solicitudExistente.setOrigenId(solicitudActualizada.getOrigenId());
        solicitudExistente.setDestinoId(solicitudActualizada.getDestinoId());
        solicitudExistente.setTarifaId(solicitudActualizada.getTarifaId());
        // Se podrían actualizar costos/tiempos estimados si se recalculan
        
        return solicitudRepository.save(solicitudExistente);
    }

    @Transactional
    public void deleteById(Integer id) {
        Solicitud solicitud = findById(id);

        if (solicitud.getEstado().getId() != ID_SOLICITUD_BORRADOR) {
            throw new RuntimeException("Solo se pueden eliminar solicitudes en estado 'borrador'.");
        }

        Contenedor contenedor = solicitud.getContenedor();

        // REVERTIR ESTADO: El contenedor vuelve a estar 'disponible'
        ContenedorEstado estadoDisponible = contenedorEstadoService.findById(ID_CONTENEDOR_DISPONIBLE);
        contenedor.setEstado(estadoDisponible);
        contenedorRepository.save(contenedor);

        // Crear seguimiento de "liberación"
        SeguimientoContenedor seguimiento = new SeguimientoContenedor(
            contenedor,
            estadoDisponible,
            solicitud.getOrigenId() // Vuelve a la ubicación de origen
        );
        seguimientoRepository.save(seguimiento);
        
        solicitudRepository.deleteById(id);
    }
}