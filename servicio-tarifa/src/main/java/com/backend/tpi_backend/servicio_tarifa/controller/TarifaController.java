package com.backend.tpi_backend.servicio_tarifa.controller;
/*
@RestController
@RequestMapping("/api/tarifas")
@RequiredArgsConstructor
public class TarifaController {
  private final TarifaService service;

  @GetMapping public List<Tarifa> listar(){ return service.listar(); }
  @PostMapping public Tarifa crear(@RequestBody Tarifa t){ return service.crear(t); }
  @GetMapping("/{id}") public Tarifa get(@PathVariable int id){ return service.get(id); }
  @DeleteMapping("/{id}") public void borrar(@PathVariable int id){ service.borrar(id); }

  @GetMapping("/vigente")
  public Tarifa vigente(@RequestParam @DateTimeFormat(iso = DATE) LocalDate fecha){
    return service.vigente(fecha);
  }

  @PostMapping("/calcular")
  public Map<String, BigDecimal> calcular(@RequestBody CalculoCostoRequest req){
    BigDecimal total = service.calcularCosto(req.km(), req.consumoLitroKm(), req.fecha());
    return Map.of("costoTotal", total);
  }
}
*/