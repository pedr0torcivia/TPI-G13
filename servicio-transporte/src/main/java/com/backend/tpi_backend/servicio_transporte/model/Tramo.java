package com.backend.tpi_backend.servicio_transporte.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "tramos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // FK interna: cada tramo pertenece a una ruta de este mismo microservicio
    @ManyToOne
    @JoinColumn(name = "ruta_id", nullable = false)
    private Ruta ruta;

    // FKs externas (a microservicio de depósitos/ubicaciones): guardamos solo el ID
    @Column(name = "origen_id", nullable = false)
    private Integer origenId;   // referencia a UBICACIONES.id

    @Column(name = "destino_id", nullable = false)
    private Integer destinoId;  // referencia a UBICACIONES.id

    // Catálogos internos del MS transporte
    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false)
    private TramoTipo tipo;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private TramoEstado estado;

    @Column(name = "costo_aproximado", precision = 10, scale = 2)
    private BigDecimal costoAproximado;

    @Column(name = "costo_real", precision = 10, scale = 2)
    private BigDecimal costoReal;

    @Column(name = "fecha_hora_inicio")
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin")
    private LocalDateTime fechaHoraFin;

    @Column(name = "fecha_hora_inicio_aprox")
    private LocalDateTime fechaHoraInicioAprox;

    @Column(name = "fecha_hora_fin_aprox")
    private LocalDateTime fechaHoraFinAprox;

    // Camión asignado (FK interna a este MS)
    @ManyToOne
    @JoinColumn(name = "camion_dominio")
    private Camion camion;

    @Column(name = "desvio_horas")
    private Double desvioHoras;

    @Column(name = "cumplio_estimacion")
    private Boolean cumplioEstimacion;
}