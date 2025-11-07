package com.backend.tpi_backend.servicio_deposito.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "ciudades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ciudad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    @Column(name = "cod_postal")
    private String codPostal;

    // Relación N:1 con Provincia
    @ManyToOne
    @JoinColumn(name = "id_provincia", nullable = false)
    private Provincia provincia;

    // Relación 1:N con Ubicacion
    @OneToMany(mappedBy = "ciudad", cascade = CascadeType.ALL)
    @JsonIgnore // Evita bucle con Ubicacion → Ciudad
    private List<Ubicacion> ubicaciones;

    // Relación 1:N con Deposito
    @OneToMany(mappedBy = "ciudad", cascade = CascadeType.ALL)
    @JsonIgnore // Evita bucle con Deposito → Ciudad
    private List<Deposito> depositos;
}
