package com.backend.tpi_backend.servicio_transporte.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Este bean intercepta CADA petición de Feign que haga este microservicio.
     * Su trabajo es tomar el token JWT de la petición entrante (la del usuario)
     * y "propagarlo" a la petición saliente (la que va a otro microservicio).
     */
    @Bean
    public RequestInterceptor requestTokenBearerInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Obtenemos los atributos de la petición actual
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    // Obtenemos el header "Authorization" de la petición original
                    String token = request.getHeader(AUTHORIZATION_HEADER);

                    if (token != null && !token.isEmpty()) {
                        // Lo copiamos a la petición Feign
                        template.header(AUTHORIZATION_HEADER, token);
                    }
                }
            }
        };
    }
}