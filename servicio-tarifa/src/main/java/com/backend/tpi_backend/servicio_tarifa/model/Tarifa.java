package com.backend.tpi_backend.servicio_tarifa.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder

public class Tarifa {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String nombre;

  private float valorLitroCombustible;

  private float costoBaseKm;

  private float costoGestionTramo;

  private LocalDate vigenteDesde;
  private LocalDate vigenteHasta; // puede ser null si sigue vigente
}
