package com.backend.tpi_backend.servicio_contenedores.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Data @NoArgsConstructor @AllArgsConstructor
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_keycloak", unique = true)
    private String idKeycloak;

    private String nombre;
    
    @Column(unique = true)
    private String email;

    private String telefono;
    private String direccion;
}