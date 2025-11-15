package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "solicitudes")
@Data @NoArgsConstructor @AllArgsConstructor
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numero;

    // Relación: Muchas solicitudes para Un contenedor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenedor_id", nullable = false)
    private Contenedor contenedor;

    // Relación: Muchas solicitudes de Un cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Relación: Muchas solicitudes tienen Un estado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_id", nullable = false)
    private SolicitudEstado estado;

    @Column(name = "costo_estimado")
    private BigDecimal costoEstimado;

    @Column(name = "tiempo_estimado")
    private Integer tiempoEstimado; // en minutos

    @Column(name = "costo_final")
    private BigDecimal costoFinal;

    @Column(name = "tiempo_real")
    private Integer tiempoReal; // en minutos

    // --- CLAVES FORÁNEAS A OTROS MICROSERVICIOS ---
    // No usamos @ManyToOne, solo guardamos el ID

    @Column(name = "origen_id", nullable = false)
    private Integer origenId; // FK a Ciudad (en DepositoService)

    @Column(name = "destino_id", nullable = false)
    private Integer destinoId; // FK a Ciudad (en DepositoService)
    
    @Column(name = "tarifa_id", nullable = false)
    private Integer tarifaId; // FK a Tarifa (en TarifaService)

    @Column(name = "ruta_id")
    private Integer rutaId;  // FK hacia MS Transporte
}