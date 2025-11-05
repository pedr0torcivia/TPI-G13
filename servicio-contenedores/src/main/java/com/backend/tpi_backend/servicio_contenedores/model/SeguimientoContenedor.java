package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seguimiento_contenedor")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SeguimientoContenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // FK -> contenedores.identificacion
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "contenedor_id", nullable = false)
    private Contenedor contenedor;

    // FK opcional -> contenedor_estado.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id")
    private EstadoContenedor estado;

    // FK externa (ciudades) como ID crudo
    @Column(name = "ubicacion_id")
    private Long ubicacionId;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;
}
