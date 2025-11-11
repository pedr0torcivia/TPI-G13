package com.backend.tpi_backend.servicio_deposito.controller;

import com.backend.tpi_backend.servicio_deposito.model.Deposito;
import com.backend.tpi_backend.servicio_deposito.services.DepositoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/depositos")
@RequiredArgsConstructor
public class DepositoController {

    private final DepositoService depositoService;

    @GetMapping
    public ResponseEntity<List<Deposito>> getAll() {
        return ResponseEntity.ok(depositoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deposito> getById(@PathVariable Integer id) {
        return depositoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Deposito> create(@RequestBody Deposito deposito) {
        return ResponseEntity.ok(depositoService.save(deposito));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deposito> update(@PathVariable Integer id, @RequestBody Deposito deposito) {
        return ResponseEntity.ok(depositoService.update(id, deposito));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        depositoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
