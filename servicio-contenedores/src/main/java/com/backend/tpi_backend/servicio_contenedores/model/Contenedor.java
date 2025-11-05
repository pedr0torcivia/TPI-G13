package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "contenedores")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificacion")
    private Long identificacion;

    @Column(name = "peso_kg", precision = 12, scale = 3)
    private BigDecimal pesoKg;

    @Column(name = "volumen_m3", precision = 12, scale = 3)
    private BigDecimal volumenM3;

    // FK -> contenedor_estado.id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado", nullable = false)
    private EstadoContenedor estado;

    // FK -> clientes.id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}
