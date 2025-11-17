package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "contenedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
// Ignorar detalles internos de Hibernate
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer identificacion;

    @Column(name = "peso_kg")
    private double pesoKg;

    @Column(name = "volumen_m3")
    private double volumenM3;

    // Muchos contenedores tienen un estado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estado", nullable = false)
    private ContenedorEstado estado;

    // Muchos contenedores pertenecen a un cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnore // ⬅⬅⬅ EVITA problemas al serializar (ciclos / proxies)
    private Cliente cliente;
}
