package com.backend.tpi_backend.servicio_contenedores.service;

import com.backend.tpi_backend.servicio_contenedores.dto.SolicitudRequestDTO;
import com.backend.tpi_backend.servicio_contenedores.model.Cliente;
import com.backend.tpi_backend.servicio_contenedores.model.Contenedor;
import com.backend.tpi_backend.servicio_contenedores.model.ContenedorEstado;
import com.backend.tpi_backend.servicio_contenedores.model.SeguimientoContenedor;
import com.backend.tpi_backend.servicio_contenedores.model.Solicitud;
import com.backend.tpi_backend.servicio_contenedores.model.SolicitudEstado;
import com.backend.tpi_backend.servicio_contenedores.repositories.ClienteRepository;
import com.backend.tpi_backend.servicio_contenedores.repositories.ContenedorRepository;
import com.backend.tpi_backend.servicio_contenedores.repositories.SeguimientoContenedorRepository;
import com.backend.tpi_backend.servicio_contenedores.repositories.SolicitudRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    // IDs de estado fijos
    private static final int ID_SOLICITUD_PROGRAMADA = 2;
    private static final int ID_CONTENEDOR_ASIGNADO = 2;
    private static final int ID_CONTENEDOR_DISPONIBLE = 1;

    private final SolicitudRepository solicitudRepository;
    private final ContenedorRepository contenedorRepository;
    private final ClienteRepository clienteRepository;
    private final SolicitudEstadoService solicitudEstadoService;
    private final ContenedorEstadoService contenedorEstadoService;
    private final SeguimientoContenedorRepository seguimientoRepository;

    public List<Solicitud> findAll() {
        return solicitudRepository.findAll();
    }

    public List<Solicitud> findByEstadoNombre(String nombreEstado) {
        return solicitudRepository.findByEstado_Nombre(nombreEstado);
    }

    public Solicitud findById(Integer id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));
    }

    public Integer findContenedorIdBySolicitudId(Integer solicitudId) {
        Solicitud solicitud = this.findById(solicitudId);
        if (solicitud.getContenedor() == null) {
            throw new RuntimeException("La solicitud " + solicitudId + " no tiene un contenedor asociado.");
        }
        return solicitud.getContenedor().getIdentificacion();
    }

    @Transactional
    protected Cliente findOrCreateCliente(Jwt jwt, SolicitudRequestDTO dto) {
        String idKeycloak = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        Cliente cliente = clienteRepository.findByIdKeycloak(idKeycloak)
                .orElse(new Cliente());

        cliente.setIdKeycloak(idKeycloak);
        cliente.setEmail(email);
        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Solicitud save(SolicitudRequestDTO dto, Jwt jwt) {

        Cliente cliente = findOrCreateCliente(jwt, dto);

        ContenedorEstado estadoContenedorAsignado = contenedorEstadoService.findById(ID_CONTENEDOR_ASIGNADO);
        SolicitudEstado estadoSolicitudProgramada = solicitudEstadoService.findById(ID_SOLICITUD_PROGRAMADA);

        Contenedor contenedor = new Contenedor();
        contenedor.setPesoKg(dto.getPesoKg());
        contenedor.setVolumenM3(dto.getVolumenM3());
        contenedor.setCliente(cliente);
        contenedor.setEstado(estadoContenedorAsignado);
        Contenedor contenedorGuardado = contenedorRepository.save(contenedor);

        Solicitud solicitud = new Solicitud();
        solicitud.setCliente(cliente);
        solicitud.setContenedor(contenedorGuardado);
        solicitud.setEstado(estadoSolicitudProgramada);
        solicitud.setOrigenId(dto.getOrigenId());
        solicitud.setDestinoId(dto.getDestinoId());
        solicitud.setTarifaId(dto.getTarifaId());
        Solicitud solicitudGuardada = solicitudRepository.save(solicitud);

        SeguimientoContenedor seguimiento = new SeguimientoContenedor(
                contenedorGuardado,
                estadoContenedorAsignado,
                dto.getOrigenId()
        );
        seguimientoRepository.save(seguimiento);

        return solicitudGuardada;
    }

    @Transactional
    public Solicitud update(Integer id, Solicitud solicitudActualizada) {
        Solicitud solicitudExistente = findById(id);

        if (solicitudExistente.getEstado().getId() != ID_SOLICITUD_PROGRAMADA) {
            throw new RuntimeException("Solo se pueden modificar solicitudes en estado 'programada'.");
        }

        solicitudExistente.setOrigenId(solicitudActualizada.getOrigenId());
        solicitudExistente.setDestinoId(solicitudActualizada.getDestinoId());
        solicitudExistente.setTarifaId(solicitudActualizada.getTarifaId());

        return solicitudRepository.save(solicitudExistente);
    }

    @Transactional
    public void deleteById(Integer id) {
        Solicitud solicitud = findById(id);

        if (solicitud.getEstado().getId() != ID_SOLICITUD_PROGRAMADA) {
            throw new RuntimeException("Solo se pueden eliminar solicitudes en estado 'programada'.");
        }

        Contenedor contenedor = solicitud.getContenedor();

        // Al eliminar la solicitud, el contenedor vuelve a estar disponible
        ContenedorEstado estadoDisponible = contenedorEstadoService.findById(ID_CONTENEDOR_DISPONIBLE);
        contenedor.setEstado(estadoDisponible);
        contenedorRepository.save(contenedor);

        SeguimientoContenedor seguimiento = new SeguimientoContenedor(
                contenedor,
                estadoDisponible,
                solicitud.getOrigenId()
        );
        seguimientoRepository.save(seguimiento);

        solicitudRepository.deleteById(id);
    }
    //metodo para el paso 4-Asingar ruta con sus tramos a solicitud
    @Transactional
    public Solicitud asignarRuta(Integer solicitudId, Integer rutaId) {

        Solicitud solicitud = findById(solicitudId);

        // estado "RUTA_ASIGNADA" (debes ver su ID real)
        SolicitudEstado estadoRutaAsignada = solicitudEstadoService.findById(3);

        solicitud.setRutaId(rutaId);
        solicitud.setEstado(estadoRutaAsignada);

        return solicitudRepository.save(solicitud);
    }

}
