package com.backend.tpi_backend.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración de Seguridad para API Gateway
 * 
 * Implementa validación de roles JWT para las rutas:
 * - CLIENTE: Consultar solicitudes y contenedores
 * - OPERADOR: Gestionar tarifas, ciudades, transportistas
 * - TRANSPORTISTA: Ver y actualizar tramos
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        // ========================================
                        // RUTAS PÚBLICAS (Sin autenticación)
                        // ========================================
                        .pathMatchers(HttpMethod.GET, "/api/clientes/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/contenedores/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/provincias/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/ciudades/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/depositos/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/ubicaciones/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/camiones/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/rutas/**").permitAll()
                        
                        // ========================================
                        // SOLICITUDES - Lectura pública
                        // ========================================
                        .pathMatchers(HttpMethod.GET, "/api/solicitudes/**").permitAll()
                        
                        // ========================================
                        // CLIENTE - Crear solicitudes
                        // ========================================
                        .pathMatchers(HttpMethod.POST, "/api/solicitudes").hasRole("CLIENTE")
                        
                        // ========================================
                        // OPERADOR - Gestión completa
                        // ========================================
                        // Tarifas (CRUD completo)
                        .pathMatchers(HttpMethod.GET, "/api/tarifas/**").hasRole("OPERADOR")
                        .pathMatchers(HttpMethod.POST, "/api/tarifas/**").hasRole("OPERADOR")
                        .pathMatchers(HttpMethod.PUT, "/api/tarifas/**").hasRole("OPERADOR")
                        .pathMatchers(HttpMethod.DELETE, "/api/tarifas/**").hasRole("OPERADOR")
                        
                        // Transportistas (Lectura solo OPERADOR)
                        .pathMatchers(HttpMethod.GET, "/api/transportistas/**").hasRole("OPERADOR")
                        .pathMatchers(HttpMethod.POST, "/api/transportistas/**").hasRole("OPERADOR")
                        .pathMatchers(HttpMethod.PUT, "/api/transportistas/**").hasRole("OPERADOR")
                        .pathMatchers(HttpMethod.DELETE, "/api/transportistas/**").hasRole("OPERADOR")
                        
                        // Crear/Actualizar rutas (Solo OPERADOR)
                        .pathMatchers(HttpMethod.POST, "/api/rutas/**").hasRole("OPERADOR")
                        .pathMatchers(HttpMethod.PUT, "/api/rutas/**").hasRole("OPERADOR")
                        .pathMatchers(HttpMethod.DELETE, "/api/rutas/**").hasRole("OPERADOR")
                        
                        // ========================================
                        // TRANSPORTISTA - Ver y actualizar tramos
                        // ========================================
                        .pathMatchers(HttpMethod.GET, "/api/tramos/**").hasAnyRole("TRANSPORTISTA", "OPERADOR")
                        .pathMatchers(HttpMethod.POST, "/api/tramos/**").hasRole("TRANSPORTISTA")
                        .pathMatchers(HttpMethod.PUT, "/api/tramos/**").hasRole("TRANSPORTISTA")
                        
                        // ========================================
                        // SOLICITUDES - Modificación (Solo OPERADOR)
                        // ========================================
                        .pathMatchers(HttpMethod.PUT, "/api/solicitudes/**").hasRole("OPERADOR")
                        .pathMatchers(HttpMethod.DELETE, "/api/solicitudes/**").hasRole("OPERADOR")
                        
                        // ========================================
                        // Cualquier otro request requiere autenticación
                        // ========================================
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
