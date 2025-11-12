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
    private static final int ID_SOLICITUD_PROGRAMADA = 2; // <-- CAMBIO DE LÓGICA (antes era BORRADOR = 1)
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

    // --- NUEVO MÉTODO ---
    // Será llamado por el controlador cuando se pida filtrar por estado
    public List<Solicitud> findByEstadoNombre(String nombreEstado) {
        return solicitudRepository.findByEstado_Nombre(nombreEstado);
    }

    public Solicitud findById(Integer id) {
        return solicitudRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Solicitud no encontrada con ID: " + id)
        );
    }

    // --- NUEVO MÉTODO (Para resolver el TODO de TramoService) ---
    /**
     * Busca una solicitud por su ID (numero) y devuelve
     * el ID (identificacion) del contenedor asociado.
     * Esto permite a servicio-transporte obtener el contenedorId.
     */
    public Integer findContenedorIdBySolicitudId(Integer solicitudId) {
        Solicitud solicitud = this.findById(solicitudId);
        if (solicitud.getContenedor() == null) {
            throw new RuntimeException("La solicitud " + solicitudId + " no tiene un contenedor asociado.");
        }
        return solicitud.getContenedor().getIdentificacion();
    }
    
    @Transactional
    public Solicitud save(Solicitud solicitud, Integer contenedorId) {
        // 1. Buscar entidades principales
        Contenedor contenedor = contenedorService.findById(contenedorId);
        
        if (contenedor.getEstado().getId() != ID_CONTENEDOR_DISPONIBLE) {
             throw new RuntimeException("El contenedor " + contenedorId + " no está disponible.");
        }
        
        Cliente cliente = contenedor.getCliente();
        // --- CAMBIO AQUÍ ---
        SolicitudEstado estadoProgramada = solicitudEstadoService.findById(ID_SOLICITUD_PROGRAMADA);

        // 2. Asignar relaciones al objeto solicitud
        solicitud.setContenedor(contenedor);
        solicitud.setCliente(cliente);
        solicitud.setEstado(estadoProgramada); // <-- CAMBIO DE LÓGICA
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

        // --- CAMBIO AQUÍ ---
        // Lógica de negocio: Solo se puede modificar una solicitud en 'programada'
        if (solicitudExistente.getEstado().getId() != ID_SOLICITUD_PROGRAMADA) {
            throw new RuntimeException("Solo se pueden modificar solicitudes en estado 'programada'.");
        }
        
        // No se puede cambiar el contenedor, pero sí el resto
        solicitudExistente.setOrigenId(solicitudActualizada.getOrigenId());
        solicitudExistente.setDestinoId(solicitudActualizada.getDestinoId());
        solicitudExistente.setTarifaId(solicitudActualizada.getTarifaId());
        
        return solicitudRepository.save(solicitudExistente);
    }

    @Transactional
    public void deleteById(Integer id) {
        Solicitud solicitud = findById(id);

        // --- CAMBIO AQUÍ ---
        // Lógica de negocio: Solo se pueden borrar solicitudes en 'programada'
        if (solicitud.getEstado().getId() != ID_SOLICITUD_PROGRAMADA) {
            throw new RuntimeException("Solo se pueden eliminar solicitudes en estado 'programada'.");
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