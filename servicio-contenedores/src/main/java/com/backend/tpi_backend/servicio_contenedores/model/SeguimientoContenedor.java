package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "seguimiento_contenedor")
@Data
@NoArgsConstructor
public class SeguimientoContenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relación: Muchos seguimientos para Un contenedor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenedor_id", nullable = false)
    private Contenedor contenedor;

    // Relación: Muchos seguimientos registran Un estado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_id")
    private ContenedorEstado estado;

    @Column(name = "fecha_hora")
    private LocalDateTime fecha;

    // --- CLAVE FORÁNEA A OTRO MICROSERVICIO ---
    
    @Column(name = "ubicacion_id")
    private Integer ubicacionId; // FK a Ciudad (en DepositoService)

    
    public SeguimientoContenedor(Contenedor contenedor, ContenedorEstado estado, Integer ubicacionId) {
        this.contenedor = contenedor;
        this.estado = estado;
        this.ubicacionId = ubicacionId;
        this.fecha = LocalDateTime.now();
    }
}