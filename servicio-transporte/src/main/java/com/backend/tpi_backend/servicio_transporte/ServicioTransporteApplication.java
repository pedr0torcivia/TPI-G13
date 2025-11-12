package com.backend.tpi_backend.servicio_transporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // <-- IMPORTAR

@SpringBootApplication
@EnableFeignClients 
public class ServicioTransporteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicioTransporteApplication.class, args);
    }

}