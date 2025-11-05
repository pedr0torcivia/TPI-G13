package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "contenedores",
       uniqueConstraints = {
         @UniqueConstraint(name = "uk_contenedor_codigo", columnNames = "codigo")
       },
       indexes = {
         @Index(name = "idx_contenedor_estado", columnList = "estado"),
         @Index(name = "idx_contenedor_cliente", columnList = "cliente_id")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "codigo")
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identificacion;  // PK

    @NotBlank
    @Column(nullable = false, length = 50)
    private String codigo;        // Ej: CONT-0001

    @Column(precision = 12, scale = 3)
    private double pesoKg;

    @Column(precision = 12, scale = 3)
    private double volumenM3;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoContenedor estado;

    @PrePersist
    void prePersist() {
        if (estado == null) estado = EstadoContenedor.DISPONIBLE;
    }

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
