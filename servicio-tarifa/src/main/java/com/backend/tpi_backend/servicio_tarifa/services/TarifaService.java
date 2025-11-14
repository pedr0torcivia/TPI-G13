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

  /* ME LLEGA LA DISTANCIA DESDE SERVICIO TRANSPORTE
  public float calcularDistancia(float latOrigen, float lonOrigen, float latDestino, float lonDestino) {
    return 0.2f; // Valor fijo para simplificar, usa api 
  }
  */

  public float calcularEstadia(int diasOcupados, float costoEstadiaDiario) {
    return diasOcupados * costoEstadiaDiario;
  }

public float calcularTarifaTramo(float volumen,
                                     float peso,
                                     float distanciaKm, // <-- PARÁMETRO AGREGADO
                                     float valorLitroCombustible,
                                     float consumoCombustible, 
                                     int diasOcupados,
                                     float costoEstadiaDiario) {

          System.out.println("--- [SERVICIO-TARIFA] CALCULANDO TARIFA ---");
          System.out.println("Volumen Recibido: " + volumen);
          System.out.println("Peso Recibido: " + peso);
          System.out.println("Distancia (km) Recibida: " + distanciaKm);
          System.out.println("Consumo Combustible (L/km): " + consumoCombustible);
          System.out.println("Valor Litro: " + valorLitroCombustible);
          System.out.println("Días Ocupados: " + diasOcupados);
          System.out.println("Costo Estadia Diario: " + costoEstadiaDiario);
          
          float estadia = calcularEstadia(diasOcupados, costoEstadiaDiario);

        // Usamos el parámetro "distanciaKm"
        if (volumen < 20 && peso < 1000) {
            float costoTramo = ((distanciaKm * consumoCombustible * valorLitroCombustible) + estadia) * 0.2f;
            return costoTramo;
        } else if (volumen < 50 && peso < 3000) {
            float costoTramo = ((distanciaKm * consumoCombustible * valorLitroCombustible) + estadia) * 0.35f;
            return costoTramo;
        } else {
            float costoTramo = ((distanciaKm * consumoCombustible * valorLitroCombustible) + estadia) * 0.5f;
            return costoTramo;
        }
    }

}
