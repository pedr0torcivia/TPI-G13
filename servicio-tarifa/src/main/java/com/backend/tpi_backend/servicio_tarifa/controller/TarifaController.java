package com.backend.tpi_backend.servicio_tarifa.controller;

import com.backend.tpi_backend.servicio_tarifa.dto.CalculoTarifaRequest;
import com.backend.tpi_backend.servicio_tarifa.model.Tarifa;
import com.backend.tpi_backend.servicio_tarifa.services.TarifaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
@RequiredArgsConstructor

public class TarifaController {
    private final TarifaService tarifaService;

    // ... (CRUDs para OPERADOR - Asumimos @PreAuthorize("hasRole('OPERADOR')") ) ...
    @GetMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<Tarifa>> getAll() {
        List<Tarifa> tarifas = tarifaService.findAll();
        return ResponseEntity.ok(tarifas);
    }
    // ... (getById, create, update, deleteById quedan igual pero DEBEN ser asegurados) ...

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Tarifa> getById(@PathVariable Integer id) {
        return tarifaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Tarifa> create(@RequestBody Tarifa tarifa) {
        Tarifa savedTarifa = tarifaService.save(tarifa);
        return ResponseEntity.ok(savedTarifa);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Tarifa> update(@PathVariable Integer id, @RequestBody Tarifa tarifa) {
        try {
            Tarifa updatedTarifa = tarifaService.update(id, tarifa);
            return ResponseEntity.ok(updatedTarifa);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        tarifaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    // --- NUEVO ENDPOINT: Deuda Técnica ---
    /**
     * Endpoint interno para que OTROS microservicios (Transporte) 
     * obtengan el valor del litro de combustible.
     * @return Valor del litro.
     */
    @GetMapping("/valor-combustible")
    @PreAuthorize("isAuthenticated()") // Basta que esté autenticado (lo llama un Feign Client)
    public ResponseEntity<Float> getValorLitroCombustible() {
        Float valor = tarifaService.getValorLitroCombustible();
        return ResponseEntity.ok(valor);
    }

    
    @PostMapping("/calcular")
@PreAuthorize("isAuthenticated()") // Lo llama servicio-transporte, debe estar autenticado
public ResponseEntity<Float> calcularTarifa(@RequestBody CalculoTarifaRequest request) {

    float tarifa = tarifaService.calcularTarifaTramo(
            request.getVolumen(),
            request.getPeso(),
            request.getDistanciaKm(),
            request.getValorLitroCombustible(),
            request.getConsumoCombustible(),
            request.getCostoKmCamion(),      
            request.getDiasOcupados(),
            request.getCostoEstadiaDiario(),
            request.getCargoGestion()       
    );

    return ResponseEntity.ok(tarifa);
}
}