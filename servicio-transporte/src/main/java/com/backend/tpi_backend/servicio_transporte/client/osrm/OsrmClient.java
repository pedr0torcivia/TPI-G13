package com.backend.tpi_backend.servicio_transporte.client.osrm;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OsrmClient {

    private final RestTemplate restTemplate;

    // configurable en application.yml
    @Value("${osrm.base-url:http://localhost:5000}")
    private String osrmBaseUrl;

    public double calcularDistanciaKm(double latOrigen, double lngOrigen,
                                      double latDestino, double lngDestino) {

        // OSRM espera: lng,lat;lng,lat
        String coords = String.format(Locale.US,
                "%f,%f;%f,%f", lngOrigen, latOrigen, lngDestino, latDestino);

        String url = osrmBaseUrl + "/route/v1/driving/" + coords + "?overview=false";

        OsrmRouteResponse response =
                restTemplate.getForObject(url, OsrmRouteResponse.class);

        if (response == null ||
            !"Ok".equalsIgnoreCase(response.getCode()) ||
            response.getRoutes() == null ||
            response.getRoutes().isEmpty()) {
            throw new IllegalStateException("No se pudo calcular ruta con OSRM");
        }

        double distanciaMetros = response.getRoutes().get(0).getDistance();
        return distanciaMetros / 1000.0; // pasamos a km
    }
}
