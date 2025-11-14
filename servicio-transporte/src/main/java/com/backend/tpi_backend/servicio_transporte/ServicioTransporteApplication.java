package com.backend.tpi_backend.servicio_transporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients; // <-- IMPORTAR
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients 
public class ServicioTransporteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicioTransporteApplication.class, args);
    }

	@Bean
		public RestTemplate restTemplate(RestTemplateBuilder builder) {
			return builder.build();
		}


}