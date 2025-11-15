package com.backend.tpi_backend.servicio_contenedores.service;

import com.backend.tpi_backend.servicio_contenedores.dto.ContenedorDTO; // <-- IMPORTAR DTO
import com.backend.tpi_backend.servicio_contenedores.dto.ContenedorPendienteDTO;
import com.backend.tpi_backend.servicio_contenedores.dto.EstadoContenedorResponse;
import com.backend.tpi_backend.servicio_contenedores.dto.SeguimientoContenedorResponse;
import com.backend.tpi_backend.servicio_contenedores.model.Cliente;
import com.backend.tpi_backend.servicio_contenedores.model.Contenedor;
import com.backend.tpi_backend.servicio_contenedores.model.ContenedorEstado;
import com.backend.tpi_backend.servicio_contenedores.model.SeguimientoContenedor;
import com.backend.tpi_backend.servicio_contenedores.model.Solicitud;
import com.backend.tpi_backend.servicio_contenedores.repositories.ContenedorRepository;
import com.backend.tpi_backend.servicio_contenedores.repositories.SeguimientoContenedorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.backend.tpi_backend.servicio_contenedores.repositories.SolicitudRepository;

import java.util.List;
import java.util.stream.Collectors; // <-- IMPORTAR STREAMS

@Service
@RequiredArgsConstructor
public class ContenedorService {

    // IDs de estado fijos
    private static final int ID_ESTADO_DISPONIBLE = 1;
    private static final int ID_ESTADO_ASIGNADO = 2;
    private static final int ID_ESTADO_EN_TRANSITO = 3;
    private static final int ID_ESTADO_ENTREGADO = 4;


    private final ContenedorRepository contenedorRepository;
    private final ClienteService clienteService;
    private final ContenedorEstadoService contenedorEstadoService;
    private final SeguimientoContenedorRepository seguimientoRepository;
    private final SolicitudRepository solicitudRepository;

    // --- NUEVO HELPER DE CONVERSIÓN ---
    private ContenedorDTO toDTO(Contenedor c) {
        ContenedorDTO dto = new ContenedorDTO();
        dto.setIdentificacion(c.getIdentificacion());
        dto.setPesoKg(c.getPesoKg());
        dto.setVolumenM3(c.getVolumenM3());
        return dto;
    }

    // --- MÉTODO 1 (para el Controller) ---
    public List<ContenedorDTO> findAllDTO() {
        return contenedorRepository.findAll()
                .stream()
                .map(this::toDTO) // Convierte cada Contenedor a ContenedorDTO
                .collect(Collectors.toList());
    }

    // Método interno que devuelve la Entidad (para uso del servicio)
    public Contenedor findById(Integer id) {
        return contenedorRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Contenedor no encontrado con ID: " + id)
        );
    }

    // --- MÉTODO 2 (para el Controller) ---
    public ContenedorDTO findDTOById(Integer id) {
        Contenedor c = this.findById(id); // Llama al método que ya tenías
        return toDTO(c); // Devuelve el DTO
    }

    public ContenedorEstado findEstadoById(Integer id) {
        Contenedor contenedor = this.findById(id);
        return contenedor.getEstado();
    }

    // --- MÉTODO 3 (para el Controller) ---
    @Transactional
    public ContenedorDTO updateEstado(Integer id, Integer nuevoEstadoId, Integer ubicacionId) {
        Contenedor contenedor = findById(id);
        ContenedorEstado nuevoEstado = contenedorEstadoService.findById(nuevoEstadoId);
        
        if (contenedor.getEstado().getId().equals(nuevoEstadoId)) {
            return toDTO(contenedor); // Devuelve el DTO del contenedor existente
        }

        contenedor.setEstado(nuevoEstado);
        Contenedor contenedorActualizado = contenedorRepository.save(contenedor);

        SeguimientoContenedor seguimiento = new SeguimientoContenedor(
            contenedorActualizado,
            nuevoEstado,
            ubicacionId 
        );
        seguimientoRepository.save(seguimiento);

        return toDTO(contenedorActualizado); // Devuelve el DTO actualizado
    }

    // --- MÉTODO 4 (para el Controller) ---
    @Transactional
    public ContenedorDTO save(Contenedor contenedor, Integer clienteId, Integer ubicacionId) {
        Cliente cliente = clienteService.findById(clienteId);
        ContenedorEstado estadoDisponible = contenedorEstadoService.findById(ID_ESTADO_DISPONIBLE);
        contenedor.setCliente(cliente);
        contenedor.setEstado(estadoDisponible);
        Contenedor contenedorGuardado = contenedorRepository.save(contenedor);
        
        SeguimientoContenedor seguimiento = new SeguimientoContenedor(
            contenedorGuardado,
            estadoDisponible,
            ubicacionId
        );
        seguimientoRepository.save(seguimiento);
        
        return toDTO(contenedorGuardado); // Devuelve el DTO guardado
    }

    // --- MÉTODO 5 (para el Controller) ---
    @Transactional
    public ContenedorDTO update(Integer id, Contenedor contenedorActualizado) {
        Contenedor contenedorExistente = findById(id);
        if (contenedorExistente.getEstado().getId() != ID_ESTADO_DISPONIBLE) {
            throw new RuntimeException("No se puede modificar un contenedor que no está 'disponible'.");
        }
        contenedorExistente.setPesoKg(contenedorActualizado.getPesoKg());
        contenedorExistente.setVolumenM3(contenedorActualizado.getVolumenM3());
        
        Contenedor guardado = contenedorRepository.save(contenedorExistente);
        return toDTO(guardado); // Devuelve el DTO actualizado
    }

    @Transactional
    public void deleteById(Integer id) {
        Contenedor contenedor = findById(id);
        int estadoId = contenedor.getEstado().getId();
        if (estadoId == ID_ESTADO_ASIGNADO || estadoId == ID_ESTADO_EN_TRANSITO) {
            throw new RuntimeException("No se puede eliminar un contenedor 'asignado' o 'en_transito'.");
        }
        seguimientoRepository.deleteAll(
             seguimientoRepository.findByContenedor_IdentificacionOrderByFechaHoraDesc(id)
        );
        contenedorRepository.deleteById(id);
    }
    //Para el paso 2-consultar estado del transporte de un contenedor
    public EstadoContenedorResponse consultarEstadoTransporte(Integer contenedorId) {

    Contenedor contenedor = this.findById(contenedorId);

    List<SeguimientoContenedor> historial =
            seguimientoRepository.findByContenedor_IdentificacionOrderByFechaHoraDesc(contenedorId);

    EstadoContenedorResponse dto = new EstadoContenedorResponse();
    dto.setContenedorId(contenedorId);
    dto.setEstadoActual(contenedor.getEstado().getNombre());

    if (!historial.isEmpty()) {
        dto.setUbicacionActualId(historial.get(0).getUbicacionId());
    }

    dto.setHistorial(
        historial.stream().map(s -> {
            SeguimientoContenedorResponse r = new SeguimientoContenedorResponse();
            r.setEstado(s.getEstado().getNombre());
            r.setUbicacionId(s.getUbicacionId());
            r.setFechaHora(s.getFechaHora());
            return r;
        }).toList()
    );

    return dto;
}

//Funcionalidad 5- . Consultar todos los contenedores pendientes de entrega y su ubicación / estado con filtros.
public List<ContenedorPendienteDTO> obtenerPendientes(Integer estadoFiltro, Integer clienteId, Integer ubicacionId) {

    // 1. Obtener todos menos ENTREGADO (id = 4)
    List<Contenedor> lista = contenedorRepository.findByEstado_IdNot(4);

    // 2. Filtrar opcionalmente
    if (estadoFiltro != null) {
        lista = lista.stream()
                .filter(c -> c.getEstado().getId().equals(estadoFiltro))
                .toList();
    }

    if (clienteId != null) {
        lista = lista.stream()
                .filter(c -> c.getCliente().getId().equals(clienteId))
                .toList();
    }

    if (ubicacionId != null) {
        lista = lista.stream()
                .filter(c -> {
                    SeguimientoContenedor seg = seguimientoRepository.findTopByContenedorOrderByFechaDesc(c);
                    return seg != null && seg.getUbicacionId().equals(ubicacionId);
                })
                .toList();
    }

    // 3. Mapear a DTO
    return lista.stream().map(c -> {
        ContenedorPendienteDTO dto = new ContenedorPendienteDTO();
        dto.setId(c.getIdentificacion());
        dto.setPesoKg(c.getPesoKg());
        dto.setVolumenM3(c.getVolumenM3());
        dto.setEstado(c.getEstado().getNombre());
        dto.setClienteNombre(c.getCliente().getNombre());

        // obtener última ubicación
        SeguimientoContenedor seg = seguimientoRepository.findTopByContenedorOrderByFechaDesc(c);
        dto.setUbicacionActualId(seg != null ? seg.getUbicacionId() : null);

        
        // obtener solicitud actual si existe
        Solicitud sol = solicitudRepository.findByContenedor(c);
        dto.setSolicitudId(sol != null ? sol.getNumero() : null);

        return dto;
    }).toList();
}



}