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
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/swagger-ui/*", "/v3/api-docs/*");
        http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // 🔓 Rutas públicas (Swagger y H2)
            .requestMatchers(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/v3/api-docs/swagger-config",
                "/h2-console/**",
                "/actuator/**"
            ).permitAll()

            // 🔐 Todo lo demás requiere JWT
            .anyRequest().authenticated()
        )
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt
                    // Llama al método que ya no es un @Bean
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    /**
     * CORRECCIÓN DE BEAN Y LECTURA DE ROLES.
     * Se remueve @Bean y se lee de 'resource_access.tpi-client.roles'.
     */
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return jwt -> new JwtAuthenticationToken(jwt, extractAuthorities(jwt));
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null) {
            return List.of();
        }

        Map<String, Object> tpiClient = (Map<String, Object>) resourceAccess.get("tpi-client");

        if (tpiClient == null || !tpiClient.containsKey("roles")) {
            return List.of();
        }

        Collection<String> roles = (Collection<String>) tpiClient.get("roles");

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }
}