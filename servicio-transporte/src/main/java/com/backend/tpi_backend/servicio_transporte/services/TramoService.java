package com.backend.tpi_backend.servicio_transporte.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import com.backend.tpi_backend.servicio_transporte.client.*;
import com.backend.tpi_backend.servicio_transporte.client.osrm.OsrmClient;
import com.backend.tpi_backend.servicio_transporte.dto.*;
import com.backend.tpi_backend.servicio_transporte.repositories.*;

import org.springframework.stereotype.Service;

import com.backend.tpi_backend.servicio_transporte.model.*;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramoService implements BaseService<Tramo, Integer> {

    // =========================================================================
    // REPOSITORIOS
    // =========================================================================
    private final TramoRepository tramoRepository;
    private final CamionRepository camionRepository;
    private final TramoEstadoRepository tramoEstadoRepository;
    private final TransportistaRepository transportistaRepository;

    // =========================================================================
    // CLIENTES FEIGN
    // =========================================================================
    private final ContenedoresClient contenedoresClient;
    private final OsrmClient osrmClient;
    private final UbicacionClient ubicacionClient;
    private final TarifaClient tarifaClient;
    private final DepositoClient depositoClient;
    private final CamionClient camionClient;

    // =========================================================================
    // CONSTANTES
    // =========================================================================
    private static final int ID_CONTENEDOR_EN_TRANSITO = 3;
    private static final int ID_CONTENEDOR_ENTREGADO = 4;

    private static final String ESTADO_ASIGNADO = "asignado";
    private static final String ESTADO_INICIADO = "iniciado";
    private static final String ESTADO_FINALIZADO = "finalizado";

    // =========================================================================
    // MÉTODOS CRUD (BaseService)
    // =========================================================================
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

    // =========================================================================
    // VALIDACIÓN DE TRANSPORTISTA
    // =========================================================================
    private void validarTransportista(Tramo tramo, String idKeycloak) {
        if (tramo.getCamion() == null) {
            throw new RuntimeException("El tramo no tiene un camión asignado.");
        }

        String idDuenio = tramo.getCamion().getTransportista().getIdKeycloak();

        if (!idKeycloak.equals(idDuenio)) {
            throw new RuntimeException("Acceso denegado. Este tramo no está asignado a su transportista.");
        }
    }

    // =========================================================================
    // REQ. 6 — ASIGNAR CAMIÓN
    // =========================================================================
    @Transactional
    public String asignarCamion(Integer idTramo, String dominioCamion) {

        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

        Camion camion = camionRepository.findById(dominioCamion)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado"));

        Ruta ruta = tramo.getRuta();

        Integer idContenedor = contenedoresClient.getContenedorIdBySolicitudId(ruta.getSolicitudId());
        if (idContenedor == null) {
            throw new RuntimeException("No se encontró contenedor asociado a la solicitud");
        }

        ContenedorDTO contenedor = contenedoresClient.getContenedor(idContenedor);

        if (contenedor.getPesoKg() > camion.getCapacidadPesoKg())
            throw new RuntimeException("El contenedor supera la capacidad de peso del camión");

        if (contenedor.getVolumenM3() > camion.getCapacidadVolumenM3())
            throw new RuntimeException("El contenedor supera la capacidad de volumen del camión");

        TramoEstado estadoAsignado = tramoEstadoRepository.findByNombre(ESTADO_ASIGNADO)
                .orElseThrow(() -> new RuntimeException("Estado 'asignado' no encontrado"));

        tramo.setCamion(camion);
        tramo.setEstado(estadoAsignado);
        camion.setDisponibilidad(false);

        tramoRepository.save(tramo);
        camionRepository.save(camion);

        return "Camión asignado correctamente";
    }

    // =========================================================================
    // INICIAR TRAMO
    // =========================================================================
    @Transactional
    public Tramo iniciarTramo(Integer idTramo) {

        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con id " + idTramo));

        TramoEstado estadoIniciado = tramoEstadoRepository.findByNombre(ESTADO_INICIADO)
                .orElseThrow(() -> new RuntimeException("Estado 'iniciado' no encontrado"));

        if (tramo.getCamion() == null)
            throw new RuntimeException("El tramo no tiene un camión asignado. No puede iniciarse.");

        tramo.setEstado(estadoIniciado);
        tramo.setFechaHoraInicio(LocalDateTime.now());
        Tramo tramoGuardado = tramoRepository.save(tramo);

        try {
            Integer solicitudId = tramo.getRuta().getSolicitudId();
            Integer idContenedor = contenedoresClient.getContenedorIdBySolicitudId(solicitudId);
            Integer idUbicacion = tramo.getOrigenId().intValue();

            contenedoresClient.updateEstado(idContenedor, ID_CONTENEDOR_EN_TRANSITO, idUbicacion);
        } catch (Exception e) {
            System.err.println("Error al actualizar estado en Contenedores (iniciarTramo): " + e.getMessage());
        }

        return tramoGuardado;
    }

    // =========================================================================
    // FINALIZAR TRAMO (incluye cálculo de costo real y desempeño)
    // =========================================================================
    @Transactional
public Tramo finalizarTramo(Integer idTramo) {

    Tramo tramo = tramoRepository.findById(idTramo)
            .orElseThrow(() -> new RuntimeException("Tramo no encontrado con id " + idTramo));

    TramoEstado estadoFinalizado = tramoEstadoRepository.findByNombre(ESTADO_FINALIZADO)
            .orElseThrow(() -> new RuntimeException("Estado 'finalizado' no encontrado"));

    if (tramo.getCamion() == null)
        throw new RuntimeException("El tramo no tiene un camión asignado. No puede finalizarse.");

    tramo.setEstado(estadoFinalizado);
    tramo.setFechaHoraFin(LocalDateTime.now());

    // ========================================================
    // REGISTRO DE DESEMPEÑO DEL TRAMO (TIEMPO ESTIMADO VS REAL)
    // ========================================================
    if (tramo.getFechaHoraInicioAprox() != null && tramo.getFechaHoraFinAprox() != null) {

        double horasEstimadas = Duration.between(
                tramo.getFechaHoraInicioAprox(),
                tramo.getFechaHoraFinAprox()
        ).toHours();

        double horasReales = Duration.between(
                tramo.getFechaHoraInicio(),
                tramo.getFechaHoraFin()
        ).toHours();

        double desvio = horasReales - horasEstimadas;

        tramo.setDesvioHoras(desvio);
        tramo.setCumplioEstimacion(desvio <= 0);
    }

    Camion camion = tramo.getCamion();
    camion.setDisponibilidad(true);
    camionRepository.save(camion);

    Tramo tramoGuardado = tramoRepository.save(tramo);

    try {
        calcularYCerrarCostoReal(tramoGuardado);
    } catch (Exception e) {
        System.err.println("Advertencia: No se pudo calcular el costo real: " + e.getMessage());
    }

    boolean esUltimoTramo = tramoRepository.findByRuta(tramo.getRuta())
            .stream()
            .filter(t -> !t.getId().equals(tramoGuardado.getId()))
            .allMatch(t -> ESTADO_FINALIZADO.equals(t.getEstado().getNombre()));

    int estadoContenedorAEnviar = esUltimoTramo ? ID_CONTENEDOR_ENTREGADO : ID_CONTENEDOR_EN_TRANSITO;

    try {
        Integer solicitudId = tramo.getRuta().getSolicitudId();
        Integer idContenedor = contenedoresClient.getContenedorIdBySolicitudId(solicitudId);
        Integer idUbicacion = tramo.getDestinoId().intValue();

        contenedoresClient.updateEstado(idContenedor, estadoContenedorAEnviar, idUbicacion);

    } catch (Exception e) {
        System.err.println("Error al actualizar estado (finalizarTramo): " + e.getMessage());
    }

    return tramoGuardado;
    }

    // =========================================================================
    // REQ. 9 — CÁLCULO DE COSTO REAL
    // =========================================================================
    @Transactional
    public BigDecimal calcularYCerrarCostoReal(Tramo tramo) {

        if (tramo.getFechaHoraInicio() == null || tramo.getFechaHoraFin() == null)
            throw new RuntimeException("Debe tener fechas reales para calcular costo.");

        Duration duracion = Duration.between(tramo.getFechaHoraInicio(), tramo.getFechaHoraFin());
        long horas = duracion.toHours();
        int diasOcupados = (horas <= 0) ? 1 : (int) Math.ceil(horas / 24.0);

        Integer solicitudId = tramo.getRuta().getSolicitudId();
        Integer idContenedor = contenedoresClient.getContenedorIdBySolicitudId(solicitudId);
        ContenedorDTO contenedor = contenedoresClient.getContenedor(idContenedor);

        Camion camion = tramo.getCamion();
        if (camion == null)
            throw new RuntimeException("Tramo sin camión asignado");

        float valorLitroCombustible = tarifaClient.getValorLitroCombustible();
        Double costoEstadiaDoble = depositoClient.getCostoEstadiaByUbicacionId(tramo.getDestinoId());
        float costoEstadiaDiario = costoEstadiaDoble != null ? costoEstadiaDoble.floatValue() : 0f;

        float distanciaKm = (float) calcularDistanciaTramoEnKm(tramo.getOrigenId(), tramo.getDestinoId());
        float consumoCombustible = camion.getConsumoCombustibleKm().floatValue();

        float costoKmPromedio = calcularPromedioCostoKm(
                contenedor.getPesoKg(),
                contenedor.getVolumenM3()
        );

        CalculoTarifaRequest request = new CalculoTarifaRequest();
        request.setVolumen((float) contenedor.getVolumenM3());
        request.setPeso((float) contenedor.getPesoKg());
        request.setDistanciaKm(distanciaKm);

        request.setValorLitroCombustible(valorLitroCombustible);
        request.setConsumoCombustible(consumoCombustible);

        request.setCostoKmCamion(costoKmPromedio);

        request.setDiasOcupados(diasOcupados);
        request.setCostoEstadiaDiario(costoEstadiaDiario);

        request.setCargoGestion(50f);

        float costoFinal = tarifaClient.calcularTarifa(request);

        BigDecimal costoRealBD = BigDecimal.valueOf(costoFinal);
        tramo.setCostoReal(costoRealBD);
        tramoRepository.save(tramo);

        return costoRealBD;
    }

    // =========================================================================
    // REQ. 8 — INFRAESTRUCTURA: DISTANCIA, y tiempo estimado entre puntos
    // =========================================================================
    public double calcularDistanciaTramoEnKm(Integer origenId, Integer destinoId) {
        UbicacionDTO origen = ubicacionClient.obtenerPorId(origenId);
        UbicacionDTO destino = ubicacionClient.obtenerPorId(destinoId);
        return osrmClient.calcularDistanciaKm(
                origen.getLat(), origen.getLng(),
                destino.getLat(), destino.getLng()
        );
    }

    public double calcularTiempoEstimado(Integer origenId, Integer destinoId) {
    UbicacionDTO origen = ubicacionClient.obtenerPorId(origenId);
    UbicacionDTO destino = ubicacionClient.obtenerPorId(destinoId);

    return osrmClient.calcularDuracionHoras(
            origen.getLat(), origen.getLng(),
            destino.getLat(), destino.getLng()
    );
}

    // construye el tramo tentativo
    public TramoTentativoDTO crearTramoTentativo(Integer origenId,
                                             Integer destinoId,
                                             Integer contenedorId) {

    // 1) Distancia estimada usando OSRM
    double distanciaKm = calcularDistanciaTramoEnKm(origenId, destinoId);

    // 2) Tiempo estimado usando OSRM (en horas)
    double tiempoHoras = calcularTiempoEstimado(origenId, destinoId);

    // 3) Costo estimado usando la lógica que ya tenés
    float costoEstimado = obtenerTarifaParaTramoEstimado(contenedorId, distanciaKm);

    // 4) Armar el DTO
    TramoTentativoDTO dto = new TramoTentativoDTO();
    dto.setOrigenId(origenId);
    dto.setDestinoId(destinoId);
    dto.setDistanciaKm(distanciaKm);
    dto.setTiempoHoras(tiempoHoras);
    dto.setCostoEstimado(costoEstimado);

    return dto;
}

    // =========================================================================
    // TARIFA ESTIMADA PARA TRAMO
    // =========================================================================
    public float obtenerTarifaParaTramoEstimado(Integer contenedorId, double distanciaKm) {

        ContenedorDTO contenedor = contenedoresClient.getContenedor(contenedorId);

        if (contenedor == null)
            throw new RuntimeException("No se encontró el contenedor con ID: " + contenedorId);

        float valorLitroCombustible = tarifaClient.getValorLitroCombustible();

        float costoKmPromedio = calcularPromedioCostoKm(
                contenedor.getPesoKg(),
                contenedor.getVolumenM3()
        );

        CalculoTarifaRequest request = new CalculoTarifaRequest();
        request.setPeso((float) contenedor.getPesoKg());
        request.setVolumen((float) contenedor.getVolumenM3());
        request.setDistanciaKm((float) distanciaKm);

        request.setValorLitroCombustible(valorLitroCombustible);
        request.setConsumoCombustible(0.35f);

        request.setCostoKmCamion(costoKmPromedio);
        request.setDiasOcupados(0);
        request.setCostoEstadiaDiario(0f);
        request.setCargoGestion(50f);

        return tarifaClient.calcularTarifa(request);
    }

    /**
 * Calcula la tarifa FINAL de un tramo REAL (no estimado).
 * Se usa cuando el tramo ya existe y pertenece a una Ruta real.
 */
public float obtenerTarifaParaTramo(Integer idTramo) {

    Tramo tramo = tramoRepository.findById(idTramo)
            .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

    float distanciaKm = (float) calcularDistanciaTramoEnKm(
            tramo.getOrigenId(),
            tramo.getDestinoId()
    );

    Integer solicitudId = tramo.getRuta().getSolicitudId();
    Integer idContenedor = contenedoresClient.getContenedorIdBySolicitudId(solicitudId);
    ContenedorDTO contenedor = contenedoresClient.getContenedor(idContenedor);

    float valorLitroCombustible = tarifaClient.getValorLitroCombustible();

    Double costoEstadiaDoble = depositoClient.getCostoEstadiaByUbicacionId(tramo.getDestinoId());
    float costoEstadiaDiario = costoEstadiaDoble != null ? costoEstadiaDoble.floatValue() : 0f;

    Camion camion = tramo.getCamion();
    if (camion == null)
        throw new RuntimeException("El tramo no tiene camión asignado");

    float consumoCombustible = camion.getConsumoCombustibleKm().floatValue();

    CalculoTarifaRequest request = new CalculoTarifaRequest();
    request.setPeso((float) contenedor.getPesoKg());
    request.setVolumen((float) contenedor.getVolumenM3());
    request.setDistanciaKm(distanciaKm);

    request.setValorLitroCombustible(valorLitroCombustible);
    request.setConsumoCombustible(consumoCombustible);

    // costo promedio de camiones elegibles (Req 8.2)
    float costoKmPromedio = calcularPromedioCostoKm(
            contenedor.getPesoKg(),
            contenedor.getVolumenM3()
    );
    request.setCostoKmCamion(costoKmPromedio);

    // estimado básico para tramos no finalizados
    request.setDiasOcupados(1);
    request.setCostoEstadiaDiario(costoEstadiaDiario);

    request.setCargoGestion(50f);

    return tarifaClient.calcularTarifa(request);
    }
    // =========================================================================
    // REQ. 8.2 — COSTO PROMEDIO POR KM (camiones elegibles)
    // =========================================================================
    public float calcularPromedioCostoKm(double peso, double volumen) {

        List<CamionDTO> camiones = camionClient.obtenerCamionesElegibles(peso, volumen);

        if (camiones.isEmpty())
            throw new RuntimeException("No hay camiones elegibles para este contenedor.");

        double promedio = camiones.stream()
                .mapToDouble(CamionDTO::getCostoKm)
                .average()
                .orElseThrow();

        return (float) promedio;
    }

}
