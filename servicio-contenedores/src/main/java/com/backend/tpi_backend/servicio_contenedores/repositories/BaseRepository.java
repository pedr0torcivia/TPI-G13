package com.backend.tpi_backend.servicio_contenedores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    // Punto de extensión: acá podés sumar métodos comunes a todos los repos
    // (ej: búsquedas paginadas estándar, helpers de auditoría, soft-delete, etc.)
}
