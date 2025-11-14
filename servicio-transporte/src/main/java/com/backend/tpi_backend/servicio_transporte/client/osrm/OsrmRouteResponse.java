package com.backend.tpi_backend.servicio_transporte.client.osrm;

import lombok.Data;
import java.util.List;

@Data
public class OsrmRouteResponse {
    private String code;
    private List<Route> routes;

    @Data
    public static class Route {
        private double distance; // en metros
        private double duration; // en segundos (si lo querés usar)
    }
}
