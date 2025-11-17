package com.backend.tpi_backend.servicio_transporte.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.tpi_backend.servicio_transporte.client.ContenedoresClient;
import com.backend.tpi_backend.servicio_transporte.dto.RutaTentativaResponse;
import com.backend.tpi_backend.servicio_transporte.dto.SolicitudDTO;
import com.backend.tpi_backend.servicio_transporte.dto.TramoTentativoDTO;
import com.backend.tpi_backend.servicio_transporte.model.Ruta;
import com.backend.tpi_backend.servicio_transporte.model.Tramo;
import com.backend.tpi_backend.servicio_transporte.model.TramoEstado;
import com.backend.tpi_backend.servicio_transporte.model.TramoTipo;
import com.backend.tpi_backend.servicio_transporte.repositories.RutaRepository;
import com.backend.tpi_backend.servicio_transporte.repositories.TramoEstadoRepository;
import com.backend.tpi_backend.servicio_transporte.repositories.TramoRepository;
import com.backend.tpi_backend.servicio_transporte.repositories.TramoTipoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RutaService implements BaseService<Ruta, Integer> {

    private final RutaRepository rutaRepository;

    // Repos y servicios necesarios para asignar ruta (Paso 4)
    private final TramoRepository tramoRepository;
    private final TramoTipoRepository tramoTipoRepository;
    private final TramoEstadoRepository tramoEstadoRepository;
    private final ContenedoresClient contenedoresClient;
    private final TramoService tramoService;

    // =========================================================
    // CRUD básico
    // =========================================================
    @Override
    public List<Ruta> findAll() {
        return rutaRepository.findAll();
    }

    @Override
    public Optional<Ruta> findById(Integer id) {
        return rutaRepository.findById(id);
    }

    @Override
    public Ruta save(Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    @Override
    public Ruta update(Integer id, Ruta ruta) {
        if (rutaRepository.existsById(id)) {
            ruta.setId(id);
            return rutaRepository.save(ruta);
        }
        throw new RuntimeException("Ruta no encontrada con id " + id);
    }

    @Override
    public void deleteById(Integer id) {
        rutaRepository.deleteById(id);
    }

    public List<Ruta> obtenerRutasPorSolicitud(Integer idSolicitud) {
        return rutaRepository.findBySolicitudId(idSolicitud);
    }

    // =========================================================
    // 🌟 MÉTODO PRINCIPAL: Asignar Ruta a una Solicitud (Paso 4)
    // =========================================================
    @Transactional
    public Ruta asignarRutaASolicitud(Integer solicitudId) {

        // 1. Obtener datos de la solicitud desde Contenedores
        SolicitudDTO solicitud = contenedoresClient.getSolicitud(solicitudId);

        if (solicitud == null) {
            throw new RuntimeException("No existe la solicitud con ID: " + solicitudId);
        }

        // 2. Crear la ruta principal (un solo tramo directo)
        Ruta ruta = new Ruta();
        ruta.setSolicitudId(solicitudId);
        ruta.setCantidadTramos(1);
        ruta.setCantidadDepositos(0);

        Ruta rutaGuardada = rutaRepository.save(ruta);

        // 3. Crear tramo inicial origen → destino
        Tramo tramo = new Tramo();
        tramo.setRuta(rutaGuardada);
        tramo.setOrigenId(solicitud.getOrigenId());
        tramo.setDestinoId(solicitud.getDestinoId());

        // Tipo: "principal"
        TramoTipo tipo = tramoTipoRepository.findByNombre("principal")
                .orElseThrow(() -> new RuntimeException("No existe el tipo de tramo 'principal'"));
        tramo.setTipo(tipo);

        // Estado inicial del tramo: "estimado" (NO 'pendiente')
        TramoEstado estado = tramoEstadoRepository.findByNombre("estimado")
                .orElseThrow(() -> new RuntimeException("No existe el estado de tramo 'estimado'"));
        tramo.setEstado(estado);

        // 4. Calcular distancia con OSRM
        double distanciaKm = tramoService.calcularDistanciaTramoEnKm(
                tramo.getOrigenId(),
                tramo.getDestinoId()
        );

        // 4b. Calcular tiempo estimado
        double tiempoHoras = tramoService.calcularTiempoEstimado(
                tramo.getOrigenId(),
                tramo.getDestinoId()
        );

        // 4c. Establecer fechas aproximadas
        LocalDateTime ahora = LocalDateTime.now();
        tramo.setFechaHoraInicioAprox(ahora);

        long horas = (long) tiempoHoras;
        long minutos = (long) ((tiempoHoras - horas) * 60);

        tramo.setFechaHoraFinAprox(
                ahora.plusHours(horas).plusMinutes(minutos)
        );

        // 5. Costo estimado
        float costo = tramoService.obtenerTarifaParaTramoEstimado(
                solicitud.getContenedorId(),
                distanciaKm
        );
        tramo.setCostoAproximado(BigDecimal.valueOf(costo));

        // 6. Guardar tramo
        tramoRepository.save(tramo);

        // 7. Avisar al MS Contenedores que la solicitud YA TIENE RUTA
        contenedoresClient.asignarRuta(solicitudId, rutaGuardada.getId());

        return rutaGuardada;
    }

    // =========================================================
    // Ruta Tentativa (ya la tenías bien)
    // =========================================================
    public RutaTentativaResponse generarRutaTentativa(Integer origenId,
                                                      Integer destinoId,
                                                      Integer contenedorId) {

        // 1) Crear el único tramo tentativo
        TramoTentativoDTO tramo = tramoService.crearTramoTentativo(
                origenId,
                destinoId,
                contenedorId
        );

        // 2) Armar respuesta de ruta
        RutaTentativaResponse response = new RutaTentativaResponse();
        response.setTramos(List.of(tramo));  // solo un tramo
        response.setDistanciaTotalKm(tramo.getDistanciaKm());
        response.setTiempoTotalHoras(tramo.getTiempoHoras());
        response.setCostoTotalEstimado(tramo.getCostoEstimado());

        return response;
    }
}
