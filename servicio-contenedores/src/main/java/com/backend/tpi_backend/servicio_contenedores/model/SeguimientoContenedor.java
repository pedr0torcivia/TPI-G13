package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "seguimiento_contenedor",
       indexes = {
         @Index(name = "idx_seguimiento_contenedor", columnList = "contenedor_id,fechaHora")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SeguimientoContenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Contenedor contenedor;

    @Column(precision = 10, scale = 6)
    private Double lat;

    @Column(precision = 10, scale = 6)
    private Double lng;

    @Column(length = 120)
    private String ciudad;

    private LocalDateTime fechaHora;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
