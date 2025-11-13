package com.backend.tpi_backend.servicio_deposito.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "estadias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estadia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 ID del contenedor (referencia al microservicio de contenedores)
    @Column(name = "id_contenedor", nullable = false)
    private Long idContenedor;

    // 🔹 Fechas de entrada y salida
    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;

    // 🔹 Estado actual del contenedor en el depósito
    // (ejemplo: "en_estadia", "listo", "en_transito")
    @Column(length = 50)
    private String estado;

    // 🔹 Relación con Depósito
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_deposito", nullable = false)
    private Deposito deposito;
}
