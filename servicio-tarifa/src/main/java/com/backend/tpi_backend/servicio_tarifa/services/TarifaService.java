package com.backend.tpi_backend.servicio_tarifa.services;
/*
@Service @RequiredArgsConstructor
public class TarifaService {
  private final TarifaRepository repo;

  public Tarifa crear(Tarifa t){ return repo.save(t); }
  public List<Tarifa> listar(){ return repo.findAll(); }
  public Tarifa get(int id){ return repo.findById(id).orElseThrow(() -> new NotFoundException("Tarifa " + id)); }
  public void borrar(int id){ repo.deleteById(id); }

  public Tarifa vigente(LocalDate fecha){
    return repo.vigenteUnica(fecha).orElseThrow(() -> new NotFoundException("Sin tarifa vigente"));
  }

  public float calcularCosto(float km, float consumoLitroKm, LocalDate fecha){
    Tarifa t = vigente(fecha);
    float combustible = km * consumoLitroKm * t.getValorLitroCombustible();
    return t.getCostoBaseKm() * km + t.getCostoGestionTramo() + combustible;
  }
}
*/