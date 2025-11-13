package com.backend.tpi_backend.servicio_deposito.services;

import com.backend.tpi_backend.servicio_deposito.model.Estadia;
import com.backend.tpi_backend.servicio_deposito.model.Deposito;
import com.backend.tpi_backend.servicio_deposito.repositories.EstadiaRepository;
import com.backend.tpi_backend.servicio_deposito.repositories.DepositoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstadiaService {

    private final EstadiaRepository estadiaRepository;
    private final DepositoRepository depositoRepository;

    // 🔹 Listar estadías activas en un depósito
    public List<Estadia> findContenedoresActivos(Integer idDeposito) {
        return estadiaRepository.findByDepositoId(idDeposito)
                .stream()
                .filter(e -> e.getFechaSalida() == null)
                .collect(Collectors.toList());
    }

    // 🔹 Filtrar contenedores por estado (campo “estado” del contenedor)
    public List<Estadia> findContenedoresByEstado(Integer idDeposito, String estado) {
        return estadiaRepository.findByDepositoId(idDeposito)
                .stream()
                .filter(e -> e.getEstado() != null && e.getEstado().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }

    // 🔹 Contenedores listos para continuar viaje (salida registrada)
    public List<Estadia> findContenedoresListos(Integer idDeposito) {
        return estadiaRepository.findByDepositoId(idDeposito)
                .stream()
                .filter(e -> e.getFechaSalida() != null)
                .collect(Collectors.toList());
    }

    // 🚚 Registrar entrada
    public Estadia registrarEntrada(Integer idDeposito, Long idContenedor) {
        Deposito deposito = depositoRepository.findById(idDeposito)
                .orElseThrow(() -> new RuntimeException("Depósito no encontrado"));

        Estadia estadia = Estadia.builder()
                .deposito(deposito)
                .idContenedor(idContenedor)
                .fechaEntrada(LocalDate.now())
                .build();

        return estadiaRepository.save(estadia);
    }

    // 🚚 Registrar salida
    public Estadia registrarSalida(Long idEstadia) {
        Estadia e = estadiaRepository.findById(idEstadia)
                .orElseThrow(() -> new RuntimeException("Estadía no encontrada"));
        e.setFechaSalida(LocalDate.now());
        return estadiaRepository.save(e);
    }

    // 💰 Calcular costo estimado por cantidad de días
    public double calcularCostoEstadia(Integer idDeposito, int dias) {
        Deposito deposito = depositoRepository.findById(idDeposito)
                .orElseThrow(() -> new RuntimeException("Depósito no encontrado"));
        return deposito.getCostoEstadia() * dias;
    }

    // 💰 Calcular costo real según fechas
    public double calcularCostoPorFechas(Integer idDeposito, Long idEstadia) {
        Estadia e = estadiaRepository.findById(idEstadia)
                .orElseThrow(() -> new RuntimeException("Estadía no encontrada"));
        long dias = ChronoUnit.DAYS.between(e.getFechaEntrada(), e.getFechaSalida());
        Deposito deposito = depositoRepository.findById(idDeposito)
                .orElseThrow(() -> new RuntimeException("Depósito no encontrado"));
        return deposito.getCostoEstadia() * dias;
    }
}
