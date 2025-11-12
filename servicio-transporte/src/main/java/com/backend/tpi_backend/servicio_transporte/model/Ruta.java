package com.backend.tpi_backend.servicio_transporte.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rutas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Referencia al ID de solicitud del microservicio Contenedores
    @Column(name = "solicitud_id", nullable = false)
    private Integer solicitudId;

    @Column(name = "cantidad_tramos")
    private Integer cantidadTramos;

    @Column(name = "cantidad_depositos")
    private Integer cantidadDepositos;

}
