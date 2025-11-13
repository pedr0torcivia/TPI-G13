package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "contenedores")
@Data @NoArgsConstructor @AllArgsConstructor
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer identificacion;

    @Column(name = "peso_kg")
    private double pesoKg;

    @Column(name = "volumen_m3")
    private double volumenM3;

    // Relación: Muchos contenedores tienen Un estado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estado", nullable = false)
    private ContenedorEstado estado;

    // Relación: Muchos contenedores pertenecen a Un cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}