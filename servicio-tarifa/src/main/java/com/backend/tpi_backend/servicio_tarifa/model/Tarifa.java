package com.backend.tpi_backend.servicio_tarifa.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tarifas")
@Data @NoArgsConstructor @AllArgsConstructor @Builder

public class Tarifa {
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "nombre")
  private String nombre;

  @Column(name = "valor_litro_combustible")
  private float valorLitroCombustible;

  @Column(name = "costo_base_km")
  private float costoBaseKm;

  @Column(name = "costo_gestion_tramo")
  private float costoGestionTramo;

  @Column(name = "vigente_desde")
  private LocalDate vigenteDesde;

  @Column(name = "vigente_hasta")
  private LocalDate vigenteHasta; // puede ser null si sigue vigente
}
