package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "clientes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // unique en DBML, lo activamos más adelante si querés
    @Column(name = "id_keycloak", length = 100)
    private String idKeycloak;

    @NotBlank
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Email
    @Column(name = "email", length = 160)
    private String email;

    @Column(name = "telefono", length = 40)
    private String telefono;

    @Column(name = "direccion", length = 200)
    private String direccion;
}
