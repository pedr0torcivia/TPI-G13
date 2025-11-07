package com.backend.tpi_backend.servicio_transporte.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tramo_estado")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TramoEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
}
