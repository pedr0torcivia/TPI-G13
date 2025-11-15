package com.backend.tpi_backend.servicio_transporte.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.backend.tpi_backend.servicio_transporte.client.ContenedoresClient;
import com.backend.tpi_backend.servicio_transporte.client.TarifaClient;
import com.backend.tpi_backend.servicio_transporte.client.UbicacionClient;
import com.backend.tpi_backend.servicio_transporte.client.osrm.OsrmClient;
import com.backend.tpi_backend.servicio_transporte.dto.ContenedorDTO;
import com.backend.tpi_backend.servicio_transporte.dto.UbicacionDTO;
import com.backend.tpi_backend.servicio_transporte.dto.CalculoTarifaRequest;

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

    // --- 2. INYECTAR CLIENTE FEIGN ---
    private final ContenedoresClient contenedoresClient;
    private final OsrmClient osrmClient;
    private final UbicacionClient ubicacionClient;
    private final TarifaClient tarifaClient;
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

    // 4. Validar capacidad del camión
    double capacidadPeso = camion.getCapacidadPesoKg().doubleValue();
    double capacidadVolumen = camion.getCapacidadVolumenM3().doubleValue();

    if (contenedor.getPesoKg() > capacidadPeso) {
        throw new RuntimeException("El contenedor supera la capacidad de peso del camión");
    }

    if (contenedor.getVolumenM3() > capacidadVolumen) {
        throw new RuntimeException("El contenedor supera la capacidad de volumen del camión");
    }

    // 5. Asignar camión al tramo
    TramoEstado estadoAsignado = tramoEstadoRepository.findByNombre("asignado")
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
        TramoEstado estadoIniciado = tramoEstadoRepository.findByNombre("iniciado")
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

        // --- 5. LLAMADA FEIGN A SERVICIO-CONTENEDORES ---
        
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

    // --- 1. NUEVO MÉTODO PARA CALCULAR DISTANCIA (de tus instrucciones) ---

    public double calcularDistanciaTramoEnKm(Integer origenId, Integer destinoId) {
        // Llama al servicio-deposito para obtener coordenadas
        UbicacionDTO origen = ubicacionClient.obtenerPorId(origenId);
        UbicacionDTO destino = ubicacionClient.obtenerPorId(destinoId);

        // Llama al OsrmClient (Docker)
        return osrmClient.calcularDistanciaKm(
                origen.getLat(), origen.getLng(),
                destino.getLat(), destino.getLng()
        );
    }


public float obtenerTarifaParaTramo(Integer idTramo) {
        // 1. Obtener el tramo
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

        // 2. Calcular distancia (usando el método nuevo)
        float distanciaKm = (float) calcularDistanciaTramoEnKm(
                tramo.getOrigenId(),
                tramo.getDestinoId()
        );

        // 3. Obtener datos del contenedor (peso, volumen)
        Integer solicitudId = tramo.getRuta().getSolicitudId();
        Integer idContenedor = contenedoresClient.getContenedorIdBySolicitudId(solicitudId);
        ContenedorDTO contenedor = contenedoresClient.getContenedor(idContenedor);

        // 4. Obtener datos del camión (consumo)
        Camion camion = tramo.getCamion();
        if (camion == null) {
            throw new RuntimeException("El tramo no tiene camión asignado");
        }
        
        // <-- CORREGIDO: Usamos el getter de tu campo BigDecimal y lo pasamos a float
        float consumoCombustible = camion.getConsumoCombustibleKm().floatValue();

        // 5. Simular otros datos (esto debería venir de algún lado)
        float valorLitroCombustible = 950.0f; // Ejemplo
        int diasOcupados = 1; // Ejemplo
        float costoEstadiaDiario = 5000.0f; // Ejemplo

        // 6. Armar la solicitud para el servicio-tarifa
        CalculoTarifaRequest request = new CalculoTarifaRequest();
        
        // <-- CORREGIDO: Casteamos el 'double' primitivo a 'float'
        request.setVolumen((float) contenedor.getVolumenM3()); 
        request.setPeso((float) contenedor.getPesoKg());
        
        request.setDistanciaKm(distanciaKm);
        request.setValorLitroCombustible(valorLitroCombustible);
        request.setConsumoCombustible(consumoCombustible);
        request.setDiasOcupados(diasOcupados);
        request.setCostoEstadiaDiario(costoEstadiaDiario);

        // 7. Llamar al servicio-tarifa y devolver el costo
        return tarifaClient.calcularTarifa(request);
    }

    // --- 2. NUEVO MÉTODO PARA TARIFA ESTIMADA (PASO 5) ---
    public float obtenerTarifaParaTramoEstimado(Integer contenedorId, double distanciaKm) {

    // 1. Obtener contenedor desde microservicio contenedores
    ContenedorDTO contenedor = contenedoresClient.getContenedor(contenedorId);

    if (contenedor == null) {
        throw new RuntimeException("No se encontró el contenedor con ID: " + contenedorId);
    }

    // 2. Armar request para el servicio de tarifas
    CalculoTarifaRequest request = new CalculoTarifaRequest();
    request.setPeso((float) contenedor.getPesoKg());
    request.setVolumen((float) contenedor.getVolumenM3());
    request.setDistanciaKm((float) distanciaKm);

    // Valores base (configurables)
    request.setValorLitroCombustible(900f);
    request.setConsumoCombustible(0.35f);   // consumo promedio sin camión
    request.setDiasOcupados(0);
    request.setCostoEstadiaDiario(0f);

    // 3. Llamar al microservicio tarifas
    return tarifaClient.calcularTarifa(request);
}

}