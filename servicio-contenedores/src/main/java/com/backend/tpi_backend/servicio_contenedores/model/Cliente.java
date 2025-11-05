package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "clientes",
       indexes = {
         @Index(name = "idx_clientes_nombre", columnList = "nombre")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reservado para futura auth (opcional único si te sirve luego)
    @Column(name = "id_keycloak", length = 80)
    private String idKeycloak;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nombre;

    @Email
    @Column(length = 160 /*, unique = true */) // <- activá unique si lo decidís ahora
    private String email;

    @Column(length = 40)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
