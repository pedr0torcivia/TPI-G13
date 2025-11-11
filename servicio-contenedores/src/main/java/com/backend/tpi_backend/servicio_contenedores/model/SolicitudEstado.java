package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "solicitud_estado")
@Data
public class SolicitudEstado {
    @Id
    private Integer id; // 1, 2, 3...
    
    private String nombre; // borrador, programada, en_transito...
}