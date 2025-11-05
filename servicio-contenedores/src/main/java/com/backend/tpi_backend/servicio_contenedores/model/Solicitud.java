package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
    name = "solicitudes",
    indexes = {
        @Index(name = "idx_solicitudes_contenedor", columnList = "contenedor_id"),
        @Index(name = "idx_solicitudes_cliente", columnList = "cliente_id"),
        @Index(name = "idx_solicitudes_estado", columnList = "estado_id")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numero;

    // FK -> contenedores.identificacion
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "contenedor_id", nullable = false)
    private Contenedor contenedor;

    // FK -> clientes.id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // DECIMAL(12,2)
    @Column(name = "costo_estimado", precision = 12, scale = 2)
    private BigDecimal costoEstimado;

    // minutos (según tu DBML)
    @Column(name = "tiempo_estimado")
    private Integer tiempoEstimado;

    // DECIMAL(12,2)
    @Column(name = "costo_final", precision = 12, scale = 2)
    private BigDecimal costoFinal;

    // minutos (según tu DBML)
    @Column(name = "tiempo_real")
    private Integer tiempoReal;

    // FK -> solicitud_estado.id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoSolicitud estado;

    // FKs a tablas fuera de alcance actual (ciudades, tarifas): se modelan como IDs crudos
    @Column(name = "origen_id", nullable = false)
    private Long origenId;

    @Column(name = "destino_id", nullable = false)
    private Long destinoId;

    @Column(name = "tarifa_id", nullable = false)
    private Long tarifaId;
}
