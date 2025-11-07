package com.backend.tpi_backend.servicio_deposito.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "depositos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deposito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    @Column(name = "costo_estadia")
    private Double costoEstadia;

    // Relación N:1 con Ciudad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciudad", nullable = false)
    @JsonIgnore // Evita recursión con Ciudad → Depositos
    private Ciudad ciudad;

    // Relación 1:1 con Ubicacion
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_ubicacion", referencedColumnName = "id", nullable = false)
    private Ubicacion ubicacion;
}
