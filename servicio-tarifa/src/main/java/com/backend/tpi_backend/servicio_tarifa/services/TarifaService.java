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

  public float calcularDistancia(float latOrigen, float lonOrigen, float latDestino, float lonDestino) {
    return 0.2f; // Valor fijo para simplificar, usa api 
  }

  public float calcularEstadia(int diasOcupados, float costoEstadiaDiario) {
    return diasOcupados * costoEstadiaDiario;
  }

  public float calcularTarifaTramo(float volumen, float peso, float valorLitroCombustible, float consumoCombustible,
  float latOrigen, float lonOrigen, float latDestino, float lonDestino, int diasOcupados, float costoEstadiaDiario) {
    if (volumen < 20 && peso < 1000) {
      float distancia = calcularDistancia(latOrigen, lonOrigen, latDestino, lonDestino);
      float estadia = calcularEstadia(diasOcupados, costoEstadiaDiario);
      float costoTramo = ((distancia * consumoCombustible * valorLitroCombustible) + estadia) * 0.2f;
      return costoTramo;
    } else if (volumen < 50 && peso < 3000) {
      float distancia = calcularDistancia(latOrigen, lonOrigen, latDestino, lonDestino);
      float estadia = calcularEstadia(diasOcupados, costoEstadiaDiario);
      float costoTramo = ((distancia * consumoCombustible * valorLitroCombustible) + estadia) * 0.35f;
      return costoTramo;
    } else {
      float distancia = calcularDistancia(latOrigen, lonOrigen, latDestino, lonDestino);
      float estadia = calcularEstadia(diasOcupados, costoEstadiaDiario);
      float costoTramo = ((distancia * consumoCombustible * valorLitroCombustible) + estadia) * 0.5f;
      return costoTramo;
    }
  }

}
