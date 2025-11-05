package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contenedor_estado")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EstadoContenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // disponible | asignado | en_transito | entregado
    @Column(name = "nombre", nullable = false, length = 40)
    private String nombre;
}
