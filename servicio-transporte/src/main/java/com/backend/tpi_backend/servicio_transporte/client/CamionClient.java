package com.backend.tpi_backend.servicio_transporte.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.backend.tpi_backend.servicio_transporte.dto.CamionDTO;

@FeignClient(name = "servicio-camiones")
public interface CamionClient {

    @GetMapping("/camiones/elegibles")
    List<CamionDTO> obtenerCamionesElegibles(
            @RequestParam("peso") double peso,
            @RequestParam("volumen") double volumen);

    @GetMapping("/camiones/{dominio}")
    CamionDTO obtenerPorDominio(@PathVariable("dominio") String dominio);
}