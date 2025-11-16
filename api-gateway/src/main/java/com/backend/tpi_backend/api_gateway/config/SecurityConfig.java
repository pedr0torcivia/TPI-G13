package com.backend.tpi_backend.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuración de Spring Security para el API Gateway (basado en WebFlux).
 * CORRECCIÓN: Se eliminó el @Bean del Converter para resolver el conflicto de tipos con WebFlux.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Define el filtro de seguridad reactivo para el Gateway.
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                // Rutas públicas permitidas: Swagger y Actuator
                .pathMatchers("/swagger-ui/**", "/api-docs/**", "/actuator/**").permitAll()
                
                // --- Reglas de Autorización Basadas en Roles ---
                .pathMatchers("/contenedores/**").hasRole("OPERADOR")
                .pathMatchers("/transporte/cliente/**").hasRole("CLIENTE")
                .pathMatchers("/transporte/driver/**").hasRole("TRANSPORTISTA")
                .pathMatchers("/deposito/**").hasAnyRole("OPERADOR", "TRANSPORTISTA")
                .pathMatchers("/tarifa/**").hasAnyRole("OPERADOR", "CLIENTE")

                // Todas las demás peticiones (que no coincidan arriba) requieren autenticación
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                // Aplica el conversor de JWT, llamando al método privado
                // Este método ya no es un @Bean, resolviendo el error de WebFlux.
                .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
            );

        return http.build();
    }

    /**
     * Define el conversor de JWT. **NO ES UN @BEAN** para evitar el conflicto.
     */
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        return jwt -> Mono.just(new JwtAuthenticationToken(jwt, extractAuthorities(jwt)));
    }

    /**
     * Lógica para extraer los roles de Keycloak.
     */
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // 1. Navega a la claim 'resource_access'
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null) {
            return List.of();
        }

        // 2. Obtiene el mapa del cliente 'tpi-client'
        Map<String, Object> tpiClient = (Map<String, Object>) resourceAccess.get("tpi-client");

        if (tpiClient == null || !tpiClient.containsKey("roles")) {
            return List.of();
        }

        // 3. Obtiene la lista de roles
        Collection<String> roles = (Collection<String>) tpiClient.get("roles");

        // 4. Convierte los roles en el formato Spring Security (e.g., "OPERADOR" -> "ROLE_OPERADOR")
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }
}