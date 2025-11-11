package com.backend.tpi_backend.servicio_contenedores.service;

import com.backend.tpi_backend.servicio_contenedores.model.Cliente;
import com.backend.tpi_backend.servicio_contenedores.model.Contenedor;
import com.backend.tpi_backend.servicio_contenedores.model.ContenedorEstado;
import com.backend.tpi_backend.servicio_contenedores.model.SeguimientoContenedor;
import com.backend.tpi_backend.servicio_contenedores.repositories.ContenedorRepository;
import com.backend.tpi_backend.servicio_contenedores.repositories.SeguimientoContenedorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContenedorService {

    // IDs de estado fijos
    private static final int ID_ESTADO_DISPONIBLE = 1;
    private static final int ID_ESTADO_ASIGNADO = 2;
    private static final int ID_ESTADO_EN_TRANSITO = 3;
    private static final int ID_ESTADO_ENTREGADO = 4; // <-- NUEVA CONSTANTE


    private final ContenedorRepository contenedorRepository;
    private final ClienteService clienteService;
    private final ContenedorEstadoService contenedorEstadoService;
    private final SeguimientoContenedorRepository seguimientoRepository;

    public List<Contenedor> findAll() {
        return contenedorRepository.findAll();
    }

    public Contenedor findById(Integer id) {
        return contenedorRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Contenedor no encontrado con ID: " + id)
        );
    }

    public ContenedorEstado findEstadoById(Integer id) {
        Contenedor contenedor = this.findById(id);
        return contenedor.getEstado();
    }

    // --- NUEVO MÉTODO TRANSACCIONAL ---
    /**
     * Actualiza el estado de un contenedor y crea un registro de seguimiento.
     * Este método será llamado por el Servicio de Transporte.
     * @param id El ID del contenedor a actualizar.
     * @param nuevoEstadoId El ID del nuevo estado (ej: 3 para "en_transito", 4 para "entregado").
     * @param ubicacionId El ID de la ciudad/depósito donde ocurre el evento.
     * @return El contenedor actualizado.
     */
    @Transactional
    public Contenedor updateEstado(Integer id, Integer nuevoEstadoId, Integer ubicacionId) {
        // 1. Buscar el contenedor
        Contenedor contenedor = findById(id);

        // 2. Buscar el nuevo objeto de estado
        ContenedorEstado nuevoEstado = contenedorEstadoService.findById(nuevoEstadoId);
        
        // 3. Validar si el estado ya está seteado (para evitar seguimientos duplicados)
        if (contenedor.getEstado().getId().equals(nuevoEstadoId)) {
            return contenedor; // Ya está en este estado, no hacer nada.
        }

        // 4. Actualizar el estado del contenedor
        contenedor.setEstado(nuevoEstado);
        Contenedor contenedorActualizado = contenedorRepository.save(contenedor);

        // 5. Crear el registro de seguimiento (Trazabilidad)
        SeguimientoContenedor seguimiento = new SeguimientoContenedor(
            contenedorActualizado,
            nuevoEstado,
            ubicacionId // La ubicación del evento (ej: inicio de tramo)
        );
        seguimientoRepository.save(seguimiento);

        return contenedorActualizado;
    }

    @Transactional
    public Contenedor save(Contenedor contenedor, Integer clienteId, Integer ubicacionId) {
        // ... (código existente de save sin cambios)
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
        return contenedorGuardado;
    }

    @Transactional
    public Contenedor update(Integer id, Contenedor contenedorActualizado) {
        // ... (código existente de update sin cambios)
        Contenedor contenedorExistente = findById(id);
        if (contenedorExistente.getEstado().getId() != ID_ESTADO_DISPONIBLE) {
            throw new RuntimeException("No se puede modificar un contenedor que no está 'disponible'.");
        }
        contenedorExistente.setPesoKg(contenedorActualizado.getPesoKg());
        contenedorExistente.setVolumenM3(contenedorActualizado.getVolumenM3());
        return contenedorRepository.save(contenedorExistente);
    }

    @Transactional
    public void deleteById(Integer id) {
        // ... (código existente de deleteById sin cambios)
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
}