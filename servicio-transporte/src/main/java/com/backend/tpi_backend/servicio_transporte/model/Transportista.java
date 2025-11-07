package com.backend.tpi_backend.servicio_transporte.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transportistas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_keycloak")
    private String idKeycloak;

    private String nombre;
    private String telefono;
    private String direccion;
}
