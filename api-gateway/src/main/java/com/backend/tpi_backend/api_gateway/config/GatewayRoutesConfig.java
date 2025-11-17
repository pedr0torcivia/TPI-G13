package com.backend.tpi_backend.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de rutas del API Gateway
 * 
 * Define las rutas a los microservicios.
 * La validación de roles se hace en SecurityConfig
 */
@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // ==========================================
                // SERVICIO CONTENEDORES
                // ==========================================
                .route("contenedores-clientes", r -> r
                        .path("/api/clientes/**")
                        .uri("http://servicio-contenedores:8081"))
                
                .route("contenedores-contenedores", r -> r
                        .path("/api/contenedores/**")
                        .uri("http://servicio-contenedores:8081"))
                
                .route("contenedores-solicitudes", r -> r
                        .path("/api/solicitudes/**")
                        .uri("http://servicio-contenedores:8081"))
                
                // ==========================================
                // SERVICIO DEPOSITO
                // ==========================================
                .route("deposito-provincias", r -> r
                        .path("/api/provincias/**")
                        .uri("http://servicio-deposito:8085"))
                
                .route("deposito-ciudades", r -> r
                        .path("/api/ciudades/**")
                        .uri("http://servicio-deposito:8085"))
                
                .route("deposito-depositos", r -> r
                        .path("/api/depositos/**")
                        .uri("http://servicio-deposito:8085"))
                
                .route("deposito-ubicaciones", r -> r
                        .path("/api/ubicaciones/**")
                        .uri("http://servicio-deposito:8085"))
                
                // ==========================================
                // SERVICIO TRANSPORTE
                // ==========================================
                .route("transporte-camiones", r -> r
                        .path("/api/camiones/**")
                        .uri("http://servicio-transporte:8082"))
                
                .route("transporte-rutas", r -> r
                        .path("/api/rutas/**")
                        .uri("http://servicio-transporte:8082"))
                
                .route("transporte-transportistas", r -> r
                        .path("/api/transportistas/**")
                        .uri("http://servicio-transporte:8082"))
                
                .route("transporte-tramos", r -> r
                        .path("/api/tramos/**")
                        .uri("http://servicio-transporte:8082"))
                
                // ==========================================
                // SERVICIO TARIFA
                // ==========================================
                .route("tarifa-all", r -> r
                        .path("/api/tarifas/**")
                        .uri("http://servicio-tarifa:8084"))
                
                .build();
    }
}

