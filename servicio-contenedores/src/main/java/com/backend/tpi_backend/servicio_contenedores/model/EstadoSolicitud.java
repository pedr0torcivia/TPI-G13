package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "solicitud_estado")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EstadoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // borrador | programada | en_transito | entregada | cancelada
    @Column(name = "nombre", nullable = false, length = 40)
    private String nombre;
}
