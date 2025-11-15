package com.backend.tpi_backend.servicio_tarifa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita la seguridad por método (ej. @PreAuthorize)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso público a Swagger
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                // Permitir acceso público a H2 Console
                .requestMatchers("/h2-console/**").permitAll()
                // Permitir acceso público a Actuator (salud del servicio)
                .requestMatchers("/actuator/**").permitAll()
                // Todas las demás peticiones requieren autenticación
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt
                    // Aquí conectamos nuestro "Traductor de Roles"
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        // H2-Console necesita esto para mostrar sus frames
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    /**
     * Este es el "Traductor de Roles" (JwtAuthenticationConverter)
     * de la página 7 de tu Apunte21-SpringSecurity.pdf.
     *
     * Lee los roles desde el claim "realm_access" de Keycloak
     * y los convierte al formato "ROLE_" que Spring Security espera.
     */
    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return new Converter<Jwt, AbstractAuthenticationToken>() {
            @Override
            public AbstractAuthenticationToken convert(Jwt jwt) {
                
                // 1. Obtener el claim "realm_access"
                Map<String, List<String>> realmAccess = jwt.getClaim("realm_access");

                if (realmAccess == null || realmAccess.isEmpty()) {
                    return new JwtAuthenticationToken(jwt, List.of());
                }

                // 2. Mapear la lista de roles a GrantedAuthority
                List<GrantedAuthority> authorities = realmAccess.get("roles")
                    .stream()
                    .map(roleName -> "ROLE_" + roleName.toUpperCase()) // 3. Agregar prefijo ROLE_
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

                // 4. Devolver el token de autenticación con los roles correctos
                return new JwtAuthenticationToken(jwt, authorities);
            }
        };
    }
}