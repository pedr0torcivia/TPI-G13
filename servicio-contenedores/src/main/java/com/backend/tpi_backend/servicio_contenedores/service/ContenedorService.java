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

    @Transactional
    public Contenedor save(Contenedor contenedor, Integer clienteId, Integer ubicacionId) {
        // 1. Buscar entidades relacionadas
        Cliente cliente = clienteService.findById(clienteId);
        ContenedorEstado estadoDisponible = contenedorEstadoService.findById(ID_ESTADO_DISPONIBLE);

        // 2. Asignar relaciones al objeto contenedor
        contenedor.setCliente(cliente);
        contenedor.setEstado(estadoDisponible);
        // El peso y volumen deben venir en el objeto 'contenedor' desde el controller
        
        // 3. Guardar el contenedor
        Contenedor contenedorGuardado = contenedorRepository.save(contenedor);

        // 4. Crear el primer seguimiento (Trazabilidad)
        SeguimientoContenedor seguimiento = new SeguimientoContenedor(
            contenedorGuardado,
            estadoDisponible,
            ubicacionId // ID de la ciudad/depósito donde se da de alta
        );
        seguimientoRepository.save(seguimiento);

        return contenedorGuardado;
    }

    @Transactional
    public Contenedor update(Integer id, Contenedor contenedorActualizado) {
        Contenedor contenedorExistente = findById(id);

        if (contenedorExistente.getEstado().getId() != ID_ESTADO_DISPONIBLE) {
            throw new RuntimeException("No se puede modificar un contenedor que no está 'disponible'.");
        }
        
        // Actualizar campos permitidos
        contenedorExistente.setPesoKg(contenedorActualizado.getPesoKg());
        contenedorExistente.setVolumenM3(contenedorActualizado.getVolumenM3());
        
        return contenedorRepository.save(contenedorExistente);
    }

    @Transactional
    public void deleteById(Integer id) {
        Contenedor contenedor = findById(id);

        int estadoId = contenedor.getEstado().getId();
        if (estadoId == ID_ESTADO_ASIGNADO || estadoId == ID_ESTADO_EN_TRANSITO) {
            throw new RuntimeException("No se puede eliminar un contenedor 'asignado' o 'en_transito'.");
        }

        // Borrar seguimientos asociados
        seguimientoRepository.deleteAll(
             seguimientoRepository.findByContenedor_IdentificacionOrderByFechaHoraDesc(id)
        );

        contenedorRepository.deleteById(id);
    }
}