package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "contenedor_estado")
@Data
public class ContenedorEstado {
    @Id
    private Integer id; // 1, 2, 3...
    
    private String nombre; // disponible, asignado, en_transito...
}