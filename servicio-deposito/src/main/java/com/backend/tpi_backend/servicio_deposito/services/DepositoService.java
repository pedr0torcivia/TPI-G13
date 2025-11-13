package com.backend.tpi_backend.servicio_deposito.services;

import com.backend.tpi_backend.servicio_deposito.model.Deposito;
import com.backend.tpi_backend.servicio_deposito.model.Ubicacion;
import com.backend.tpi_backend.servicio_deposito.repositories.CiudadRepository;
import com.backend.tpi_backend.servicio_deposito.repositories.DepositoRepository;
import com.backend.tpi_backend.servicio_deposito.repositories.UbicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class DepositoService implements BaseService<Deposito, Integer> {

    private final DepositoRepository depositoRepository;
    private final CiudadRepository ciudadRepository;
    private final UbicacionRepository ubicacionRepository;

    @Override
    public List<Deposito> findAll() {
        return depositoRepository.findAll();
    }

    @Override
    public Optional<Deposito> findById(Integer id) {
        return depositoRepository.findById(id);
    }

    @Override
    public Deposito save(Deposito deposito) {
        // Validación básica: se requiere ciudad o una ubicacion con ciudad
        boolean tieneCiudadEnDeposito = deposito.getCiudad() != null && deposito.getCiudad().getId() != null;
        boolean tieneUbicacionConCiudad = deposito.getUbicacion() != null && (
                (deposito.getUbicacion().getId() != null) ||
                        (deposito.getUbicacion().getCiudad() != null && deposito.getUbicacion().getCiudad().getId() != null)
        );
        if (!tieneCiudadEnDeposito && !tieneUbicacionConCiudad) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere 'ciudad.id' o 'ubicacion' con 'ciudad.id'");
        }

        // 🔹 Resolver ciudad del depósito si viene
        if (tieneCiudadEnDeposito) {
            Integer ciudadId = deposito.getCiudad().getId();
            var ciudad = ciudadRepository.findById(ciudadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciudad no encontrada con id " + ciudadId));
            deposito.setCiudad(ciudad);
        }

        // 🔹 Resolver ubicación (nueva o existente)
        if (deposito.getUbicacion() != null) {
            Ubicacion ubicacion = deposito.getUbicacion();

            if (ubicacion.getId() != null) {
                // Ubicación existente
                Integer ubicacionId = ubicacion.getId();
                ubicacion = ubicacionRepository.findById(ubicacionId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ubicación no encontrada con id " + ubicacionId));
            } else {
                // Ubicación nueva
                if (ubicacion.getCiudad() != null && ubicacion.getCiudad().getId() != null) {
                    Integer ciudadId = ubicacion.getCiudad().getId();
                    var ciudadUbicacion = ciudadRepository.findById(ciudadId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciudad no encontrada con id " + ciudadId));
                    ubicacion.setCiudad(ciudadUbicacion);
                } else if (deposito.getCiudad() != null) {
                    // Si no tiene ciudad propia, usar la del depósito
                    ubicacion.setCiudad(deposito.getCiudad());
                }

                // Guardar antes de asociar
                ubicacion = ubicacionRepository.save(ubicacion);
            }

            deposito.setUbicacion(ubicacion);
        }

        return depositoRepository.save(deposito);
    }

    @Override
    public Deposito update(Integer id, Deposito deposito) {
        if (!depositoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Depósito no encontrado con id " + id);
        }

        deposito.setId(id);
        return save(deposito); // Reutiliza la misma lógica de save()
    }

    @Override
    public void deleteById(Integer id) {
        depositoRepository.deleteById(id);
    }
}
