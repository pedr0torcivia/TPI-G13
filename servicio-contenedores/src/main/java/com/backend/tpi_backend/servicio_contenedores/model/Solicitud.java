package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "solicitudes",
       indexes = {
         @Index(name = "idx_solicitud_estado", columnList = "estado"),
         @Index(name = "idx_solicitud_contenedor", columnList = "contenedor_id"),
         @Index(name = "idx_solicitud_cliente", columnList = "cliente_id")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numero;   // PK

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Contenedor contenedor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cliente cliente;

    @Column(precision = 12, scale = 2)
    private BigDecimal costoEstimado;

    @Column(precision = 12, scale = 2)
    private BigDecimal costoFinal;

    @PositiveOrZero
    private Integer tiempoEstimado;  // horas

    @PositiveOrZero
    private Integer tiempoReal;      // horas

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitud estado;

    @PrePersist
    void prePersist() {
        if (estado == null) estado = EstadoSolicitud.BORRADOR;
    }

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
