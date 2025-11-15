package com.backend.tpi_backend.servicio_tarifa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Swagger
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // Actuator
                .requestMatchers("/actuator/**").permitAll()

                // H2 Console (solo si lo querés habilitar)
                .requestMatchers("/h2-console/**").permitAll()

                // Todo lo demás protegido
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));

        // Permite que el H2-console funcione
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
