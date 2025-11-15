package com.backend.tpi_backend.servicio_transporte.services;

import com.backend.tpi_backend.servicio_transporte.client.UbicacionClient;
import com.backend.tpi_backend.servicio_transporte.client.osrm.OsrmClient;
import com.backend.tpi_backend.servicio_transporte.client.TarifaClient;
import com.backend.tpi_backend.servicio_transporte.client.ContenedoresClient;

import com.backend.tpi_backend.servicio_transporte.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RutaPlanificacionService {

    private final UbicacionClient ubicacionClient;
    private final OsrmClient osrmClient;
    private final TarifaClient tarifaClient;
    private final ContenedoresClient contenedoresClient;

    private static final double VELOCIDAD_PROMEDIO_KM_H = 85.0;

    public RutaTentativaResponse calcularRutaTentativa(Integer origenId, Integer destinoId, Integer contenedorId) {

        List<TramoTentativoDTO> tramos = new ArrayList<>();

        // ➤ En esta versión simplificada: solo un tramo origen → destino
        UbicacionDTO origen = ubicacionClient.obtenerPorId(origenId);
        UbicacionDTO destino = ubicacionClient.obtenerPorId(destinoId);

        double distanciaKm = osrmClient.calcularDistanciaKm(
                origen.getLat(), origen.getLng(),
                destino.getLat(), destino.getLng()
        );

        double tiempoHoras = distanciaKm / VELOCIDAD_PROMEDIO_KM_H;

        // Obtener contenedor
        ContenedorDTO contenedor = contenedoresClient.getContenedor(contenedorId);

        // Armar request para tarifa
        CalculoTarifaRequest req = new CalculoTarifaRequest();
        req.setPeso((float) contenedor.getPesoKg());
        req.setVolumen((float) contenedor.getVolumenM3());
        req.setDistanciaKm((float) distanciaKm);
        req.setValorLitroCombustible(900);
        req.setConsumoCombustible(0.35f);
        req.setDiasOcupados(0);
        req.setCostoEstadiaDiario(0);

        float costo = tarifaClient.calcularTarifa(req);

        // Armamos el tramo
        TramoTentativoDTO tramo = new TramoTentativoDTO();
        tramo.setOrigenId(origenId);
        tramo.setDestinoId(destinoId);
        tramo.setDistanciaKm(distanciaKm);
        tramo.setTiempoHoras(tiempoHoras);
        tramo.setCostoEstimado(costo);

        tramos.add(tramo);

        // Armamos la respuesta final
        RutaTentativaResponse resp = new RutaTentativaResponse();
        resp.setTramos(tramos);
        resp.setDistanciaTotalKm(distanciaKm);
        resp.setTiempoTotalHoras(tiempoHoras);
        resp.setCostoTotalEstimado(costo);

        return resp;
    }
}
