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

    // Simula el contenedor (por ahora sin FK real, ya que pertenece al microservicio contenedores)
    @Column(name = "id_contenedor", nullable = false)
    private Long idContenedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_deposito", nullable = false)
    private Deposito deposito;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;
}
