package com.backend.tpi_backend.servicio_transporte.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "camiones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Camion {

    @Id
    @Column(length = 20)
    private String dominio; // PK: patente del camión

    @ManyToOne
    @JoinColumn(name = "id_transportista")
    private Transportista transportista;

    @Column(name = "capacidad_peso_kg", precision = 12, scale = 3)
    private double capacidadPesoKg;

    @Column(name = "capacidad_volumen_m3", precision = 12, scale = 3)
    private double capacidadVolumenM3;

    private Boolean disponibilidad;

    @Column(name = "costo_km", precision = 10, scale = 2)
    private BigDecimal costoKm;

    @Column(name = "consumo_combustible_km", precision = 10, scale = 3)
    private BigDecimal consumoCombustibleKm;
}