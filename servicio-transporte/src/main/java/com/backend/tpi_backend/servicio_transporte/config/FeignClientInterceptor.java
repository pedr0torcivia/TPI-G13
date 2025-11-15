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
     * Este es el "propagador" de tokens.
     * Antes de que Feign (servicio-transporte) llame a otro microservicio 
     * (contenedores, deposito, etc.), este interceptor agarra el token
     * de la llamada original (la de Postman) y se lo pega a la nueva llamada.
     */
    @Bean
    public RequestInterceptor requestTokenBearerInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 1. Agarra la petición actual (la de Postman)
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    
                    // 2. Busca el token "Authorization"
                    String token = request.getHeader(AUTHORIZATION_HEADER);

                    if (token != null && !token.isEmpty()) {
                        // 3. Se lo pega a la llamada Feign
                        template.header(AUTHORIZATION_HEADER, token);
                    }
                }
            }
        };
    }
}