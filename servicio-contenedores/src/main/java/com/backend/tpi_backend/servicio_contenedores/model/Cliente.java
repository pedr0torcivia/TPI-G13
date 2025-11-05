package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idKeycloak;

    @NotBlank
    private String nombre;

    @Email
    private String email;

    private String telefono;
    private String direccion;
}
