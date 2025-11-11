package com.backend.tpi_backend.servicio_deposito.services;

import com.backend.tpi_backend.servicio_deposito.model.Estadia;
import com.backend.tpi_backend.servicio_deposito.repositories.EstadiaRepository;
import com.backend.tpi_backend.servicio_deposito.repositories.DepositoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadiaService {

    private final EstadiaRepository estadiaRepository;
    private final DepositoRepository depositoRepository;

    public List<Estadia> findByDeposito(Integer idDeposito) {
        return estadiaRepository.findByDepositoId(idDeposito);
    }

    public double calcularCostoEstadia(Integer idDeposito, int dias) {
        var deposito = depositoRepository.findById(idDeposito)
                .orElseThrow(() -> new RuntimeException("Depósito no encontrado"));
        return deposito.getCostoEstadia() * dias;
    }

    public double calcularCostoPorFechas(Integer idDeposito, Long idEstadia) {
        Estadia e = estadiaRepository.findById(idEstadia)
                .orElseThrow(() -> new RuntimeException("Estadía no encontrada"));

        long dias = ChronoUnit.DAYS.between(e.getFechaEntrada(), e.getFechaSalida());
        var deposito = depositoRepository.findById(idDeposito)
                .orElseThrow(() -> new RuntimeException("Depósito no encontrado"));

        return deposito.getCostoEstadia() * dias;
    }
}
