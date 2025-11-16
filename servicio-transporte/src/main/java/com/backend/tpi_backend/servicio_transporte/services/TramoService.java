package com.backend.tpi_backend.servicio_transporte.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import com.backend.tpi_backend.servicio_transporte.client.ContenedoresClient;
import com.backend.tpi_backend.servicio_transporte.client.TarifaClient;
import com.backend.tpi_backend.servicio_transporte.client.UbicacionClient;
import com.backend.tpi_backend.servicio_transporte.client.DepositoClient;
import com.backend.tpi_backend.servicio_transporte.client.osrm.OsrmClient;
import com.backend.tpi_backend.servicio_transporte.dto.ContenedorDTO;
import com.backend.tpi_backend.servicio_transporte.dto.UbicacionDTO;
import com.backend.tpi_backend.servicio_transporte.dto.CalculoTarifaRequest;
import com.backend.tpi_backend.servicio_transporte.repositories.TransportistaRepository;

import org.springframework.stereotype.Service;

import com.backend.tpi_backend.servicio_transporte.model.Camion;
import com.backend.tpi_backend.servicio_transporte.model.Ruta;
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
    private final TransportistaRepository transportistaRepository;

    // --- CLIENTES ---
    private final ContenedoresClient contenedoresClient;
    private final OsrmClient osrmClient;
    private final UbicacionClient ubicacionClient;
    private final TarifaClient tarifaClient;
    private final DepositoClient depositoClient;

    // --- Constantes de ESTADO ---
    private static final int ID_CONTENEDOR_EN_TRANSITO = 3;
    private static final int ID_CONTENEDOR_ENTREGADO = 4;
    private static final String ESTADO_ASIGNADO = "asignado";
    private static final String ESTADO_INICIADO = "iniciado";
    private static final String ESTADO_FINALIZADO = "finalizado";

    // =========================================================================
    // MÉTODOS OBLIGATORIOS (CRUD BASE)
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
    // LÓGICA DE SEGURIDAD (Validar Transportista)
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
    // REQUERIMIENTO #6: ASIGNAR CAMIÓN (OPERADOR)
    // =========================================================================
    @Transactional
    public String asignarCamion(Integer idTramo, String dominioCamion) {

        // 1. Obtener tramo y camión
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

        Camion camion = camionRepository.findById(dominioCamion)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado"));

        Ruta ruta = tramo.getRuta();

        // 2. Obtener ID del contenedor desde solicitud
        Integer idContenedor = contenedoresClient.getContenedorIdBySolicitudId(ruta.getSolicitudId());
        if (idContenedor == null) {
            throw new RuntimeException("No se encontró contenedor asociado a la solicitud");
        }

        // 3. Obtener contenedor completo
        ContenedorDTO contenedor = contenedoresClient.getContenedor(idContenedor);

        // 4. Validar capacidad del camión (Req. 11)
        double capacidadPeso = camion.getCapacidadPesoKg().doubleValue();
        double capacidadVolumen = camion.getCapacidadVolumenM3().doubleValue();

        if (contenedor.getPesoKg() > capacidadPeso) {
            throw new RuntimeException("El contenedor supera la capacidad de peso del camión");
        }

        if (contenedor.getVolumenM3() > capacidadVolumen) {
            throw new RuntimeException("El contenedor supera la capacidad de volumen del camión");
        }

        // 5. Asignar camión al tramo
        TramoEstado estadoAsignado = tramoEstadoRepository.findByNombre(ESTADO_ASIGNADO)
                .orElseThrow(() -> new RuntimeException("Estado 'asignado' no encontrado"));

        tramo.setCamion(camion);
        tramo.setEstado(estadoAsignado);
        camion.setDisponibilidad(false);

        // 6. Guardar
        tramoRepository.save(tramo);
        camionRepository.save(camion);

        return "Camión asignado correctamente";
    }

    @Transactional
    public Tramo iniciarTramo(Integer idTramo) {
        // 1. Lógica local
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con id " + idTramo));
        TramoEstado estadoIniciado = tramoEstadoRepository.findByNombre(ESTADO_INICIADO)
                .orElseThrow(() -> new RuntimeException("Estado 'iniciado' no encontrado"));
        if (tramo.getCamion() == null) {
            throw new RuntimeException("El tramo no tiene un camión asignado. No puede iniciarse.");
        }
        tramo.setEstado(estadoIniciado);
        tramo.setFechaHoraInicio(LocalDateTime.now());
        Tramo tramoGuardado = tramoRepository.save(tramo);

        // --- 4. LLAMADA FEIGN A SERVICIO-CONTENEDORES ---
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
            // Opcional: revertir estado local si la llamada es crítica
        }

        return tramoGuardado;
    }

    @Transactional
    public Tramo finalizarTramo(Integer idTramo) {
        // 1. Lógica local
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con id " + idTramo));
        TramoEstado estadoFinalizado = tramoEstadoRepository.findByNombre(ESTADO_FINALIZADO)
                .orElseThrow(() -> new RuntimeException("Estado 'finalizado' no encontrado"));
        if (tramo.getCamion() == null) {
            throw new RuntimeException("El tramo no tiene un camión asignado. No puede finalizarse.");
        }
        tramo.setEstado(estadoFinalizado);
        tramo.setFechaHoraFin(LocalDateTime.now());
        Camion camion = tramo.getCamion();
        camion.setDisponibilidad(true);
        camionRepository.save(camion);
        Tramo tramoGuardado = tramoRepository.save(tramo); // Persiste fechas reales

        // --- NUEVO PASO: CERRAR EL COSTO REAL (Req. 9) ---
        try {
            calcularYCerrarCostoReal(tramoGuardado);
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo calcular el costo real al finalizar el tramo: " + e.getMessage());
        }

        // --- 5. LLAMADA FEIGN A SERVICIO-CONTENEDORES ---

        // PASO 5.1: Verificar si este es el último tramo de la ruta
        boolean esUltimoTramo = true;
        List<Tramo> tramosDeLaRuta = tramoRepository.findByRuta(tramo.getRuta());

        for (Tramo t : tramosDeLaRuta) {
            // Si encontramos CUALQUIER otro tramo que no esté "finalizado",
            // significa que este NO es el último.
            if (!t.getId().equals(tramoGuardado.getId())
                    && !ESTADO_FINALIZADO.equals(t.getEstado().getNombre())) {
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

    // =========================================================================
    // REQUERIMIENTO #9: MÉTODO DE CÁLCULO DE COSTO REAL (NUEVO)
    // =========================================================================

    /**
     * Calcula el costo real del tramo (incluyendo estadía real) y persiste el resultado.
     * Implementa Req. 8 (parte tramo) y central del Req. 9.
     */
    @Transactional
    public BigDecimal calcularYCerrarCostoReal(Tramo tramo) {
        if (tramo.getFechaHoraInicio() == null || tramo.getFechaHoraFin() == null) {
            throw new RuntimeException("El tramo debe tener fechas de inicio y fin reales para calcular el costo.");
        }

        // 1. Calcular Días Ocupados (Estadía Real - Req. 8.3)
        Duration duracion = Duration.between(tramo.getFechaHoraInicio(), tramo.getFechaHoraFin());
        long horas = duracion.toHours();
        int diasOcupados;

        if (horas <= 0) {
            diasOcupados = 1; // mínimo 1 día
        } else {
            diasOcupados = (int) Math.ceil(horas / 24.0);
        }

        // 2. Obtener Parámetros de Clientes (Req. 8)
        Integer solicitudId = tramo.getRuta().getSolicitudId();
        Integer idContenedor = contenedoresClient.getContenedorIdBySolicitudId(solicitudId);
        ContenedorDTO contenedor = contenedoresClient.getContenedor(idContenedor);

        Camion camion = tramo.getCamion();
        if (camion == null) {
            throw new RuntimeException("Tramo sin camión para calcular costo real.");
        }

        // 3. Clientes Feign
        float valorLitroCombustible = tarifaClient.getValorLitroCombustible();
        Double costoEstadiaDoble = depositoClient.getCostoEstadiaByUbicacionId(tramo.getDestinoId());
        float costoEstadiaDiario = costoEstadiaDoble != null ? costoEstadiaDoble.floatValue() : 0f;

        // 4. Distancia (Req. 8.1)
        float distanciaKm = (float) calcularDistanciaTramoEnKm(tramo.getOrigenId(), tramo.getDestinoId());
        float consumoCombustible = camion.getConsumoCombustibleKm().floatValue();

        // 5. Preparar Request y llamar a TarifaService
        CalculoTarifaRequest request = new CalculoTarifaRequest();
        request.setVolumen((float) contenedor.getVolumenM3());
        request.setPeso((float) contenedor.getPesoKg());
        request.setDistanciaKm(distanciaKm);
        request.setValorLitroCombustible(valorLitroCombustible);
        request.setConsumoCombustible(consumoCombustible);
        request.setDiasOcupados(diasOcupados);
        request.setCostoEstadiaDiario(costoEstadiaDiario);

        float costoFinal = tarifaClient.calcularTarifa(request);

        // 6. Persistir el Costo Real (Req. 9)
        BigDecimal costoRealBD = BigDecimal.valueOf(costoFinal);
        tramo.setCostoReal(costoRealBD);
        tramoRepository.save(tramo);

        // TODO: Llamar a SolicitudService para acumular y actualizar el costo total real de la Solicitud.

        return costoRealBD;
    }

    // =========================================================================
    // REQUERIMIENTO #8: CÁLCULO DE COSTOS (INFRAESTRUCTURA)
    // =========================================================================

    public double calcularDistanciaTramoEnKm(Integer origenId, Integer destinoId) {
        UbicacionDTO origen = ubicacionClient.obtenerPorId(origenId);
        UbicacionDTO destino = ubicacionClient.obtenerPorId(destinoId);
        return osrmClient.calcularDistanciaKm(
                origen.getLat(), origen.getLng(),
                destino.getLat(), destino.getLng()
        );
    }

    public float obtenerTarifaParaTramo(Integer idTramo) {
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

        float distanciaKm = (float) calcularDistanciaTramoEnKm(tramo.getOrigenId(), tramo.getDestinoId());
        Integer solicitudId = tramo.getRuta().getSolicitudId();
        Integer idContenedor = contenedoresClient.getContenedorIdBySolicitudId(solicitudId);
        ContenedorDTO contenedor = contenedoresClient.getContenedor(idContenedor);

        // OBTENER VALORES REALES
        float valorLitroCombustible = tarifaClient.getValorLitroCombustible();

        // Uso el destino como ubicación, asumiendo que el Depósito está en el destino del tramo
        Double costoEstadiaDoble = depositoClient.getCostoEstadiaByUbicacionId(tramo.getDestinoId());
        float costoEstadiaDiario = costoEstadiaDoble != null ? costoEstadiaDoble.floatValue() : 0f;

        Camion camion = tramo.getCamion();
        if (camion == null) {
            throw new RuntimeException("El tramo no tiene camión asignado");
        }

        float consumoCombustible = camion.getConsumoCombustibleKm().floatValue();

        int diasOcupados = 1; // estimado/base

        CalculoTarifaRequest request = new CalculoTarifaRequest();
        request.setVolumen((float) contenedor.getVolumenM3());
        request.setPeso((float) contenedor.getPesoKg());
        request.setDistanciaKm(distanciaKm);
        request.setValorLitroCombustible(valorLitroCombustible);
        request.setConsumoCombustible(consumoCombustible);
        request.setDiasOcupados(diasOcupados);
        request.setCostoEstadiaDiario(costoEstadiaDiario);

        return tarifaClient.calcularTarifa(request);
    }

    // =========================================================================
    // LÓGICA DE CÁLCULO DE TARIFA ESTIMADA
    // =========================================================================

    /**
     * Calcula una tarifa estimada inicial para una ruta o tramo, asumiendo valores promedio
     * de consumo y sin considerar estadía.
     */
    public float obtenerTarifaParaTramoEstimado(Integer contenedorId, double distanciaKm) {

        ContenedorDTO contenedor = contenedoresClient.getContenedor(contenedorId);

        if (contenedor == null) {
            throw new RuntimeException("No se encontró el contenedor con ID: " + contenedorId);
        }

        float valorLitroCombustible = tarifaClient.getValorLitroCombustible();

        CalculoTarifaRequest request = new CalculoTarifaRequest();
        request.setPeso((float) contenedor.getPesoKg());
        request.setVolumen((float) contenedor.getVolumenM3());
        request.setDistanciaKm((float) distanciaKm);

        request.setValorLitroCombustible(valorLitroCombustible);
        request.setConsumoCombustible(0.35f); // consumo promedio
        request.setDiasOcupados(0);
        request.setCostoEstadiaDiario(0f);

        return tarifaClient.calcularTarifa(request);
    }
}
