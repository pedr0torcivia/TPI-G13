package com.backend.tpi_backend.servicio_transporte.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tramo_tipo")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TramoTipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
}