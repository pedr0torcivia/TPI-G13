package com.backend.tpi_backend.servicio_tarifa.services;

import com.backend.tpi_backend.servicio_tarifa.model.Tarifa;
import com.backend.tpi_backend.servicio_tarifa.repositories.TarifaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
// import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TarifaService implements BaseService<Tarifa, Integer> {
  private final TarifaRepository tarifaRepository;

  @Override
  public List<Tarifa> findAll() {
    return tarifaRepository.findAll();
  }

  @Override
  public Optional<Tarifa> findById(Integer id) {
    return tarifaRepository.findById(id);
  }

  @Override
  public Tarifa save(Tarifa entity) {
    return tarifaRepository.save(entity);
  }

  @Override
  public Tarifa update(Integer id, Tarifa entity) {
    if (tarifaRepository.existsById(id)) {
      entity.setId(id);
      return tarifaRepository.save(entity);
    } else {
      throw new RuntimeException("Tarifa no encontrada con id " + id);
    }
  }

  @Override
  public void deleteById(Integer id) {
    tarifaRepository.deleteById(id);
  }

  /*
   * ME LLEGA LA DISTANCIA DESDE SERVICIO TRANSPORTE
   * public float calcularDistancia(float latOrigen, float lonOrigen, float
   * latDestino, float lonDestino) {
   * return 0.2f; // Valor fijo para simplificar, usa api
   * }
   */

  public Float getValorLitroCombustible() {
    // En una implementación real, buscarías la tarifa por fecha de vigencia.
    // Aquí asumimos la Tarifa ID=1 es la activa.
    Tarifa tarifa = tarifaRepository.findById(1)
        .orElseThrow(() -> new RuntimeException("Tarifa base (ID 1) no encontrada para Valor Litro."));

    return tarifa.getValorLitroCombustible();
  }

  public float calcularEstadia(int diasOcupados, float costoEstadiaDiario) {
    return diasOcupados * costoEstadiaDiario;
  }

  public float calcularTarifaTramo(
      float volumen,
      float peso,
      float distanciaKm,
      float valorLitroCombustible,
      float consumoCombustible,
      float costoKmCamion,
      int diasOcupados,
      float costoEstadiaDiario,
      float cargoGestion) {

    System.out.println("--- [SERVICIO-TARIFA] CALCULANDO TARIFA ---");
    System.out.println("Volumen: " + volumen);
    System.out.println("Peso: " + peso);
    System.out.println("Distancia: " + distanciaKm);
    System.out.println("Consumo: " + consumoCombustible);
    System.out.println("Costo x KM Camion: " + costoKmCamion);
    System.out.println("Dias Ocupados: " + diasOcupados);
    System.out.println("Costo Estadia Diario: " + costoEstadiaDiario);
    System.out.println("Cargo Gestión: " + cargoGestion);

    // 1. Costos base
    float costoCombustible = distanciaKm * consumoCombustible * valorLitroCombustible;
    float costoKm = distanciaKm * costoKmCamion;
    float costoEstadia = diasOcupados * costoEstadiaDiario;

    // 2. Subtotal antes del ajuste
    float subtotal = costoCombustible + costoKm + costoEstadia + cargoGestion;

    // 3. Ajuste por capacidad → regla del TPI
    float factor;
    if (volumen < 20 && peso < 1000)
      factor = 1.10f;
    else if (volumen < 50 && peso < 3000)
      factor = 1.30f;
    else
      factor = 1.60f;

    // 4. Cálculo final
    float total = subtotal * factor;

    System.out.println("Subtotal: " + subtotal);
    System.out.println("Factor: " + factor);
    System.out.println("TOTAL: " + total);

    return total;
  }
}
