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

  private String nombre;

  private float valorLitroCombustible;

  private float costoBaseKm;

  private float costoGestionTramo;

  private LocalDate vigenteDesde;
  private LocalDate vigenteHasta; // puede ser null si sigue vigente
}
