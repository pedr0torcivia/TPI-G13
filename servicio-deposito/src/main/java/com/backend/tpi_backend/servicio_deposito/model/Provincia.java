package com.backend.tpi_backend.servicio_deposito.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "provincias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provincia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    // Relación 1:N con Ciudad
    @OneToMany(mappedBy = "provincia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ciudad> ciudades;
}
