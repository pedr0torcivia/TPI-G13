package com.backend.tpi_backend.servicio_contenedores.service;

import com.backend.tpi_backend.servicio_contenedores.model.Cliente;
import com.backend.tpi_backend.servicio_contenedores.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    public List<Cliente> findAll() {
        return repository.findAll();
    }

    public Cliente findById(Integer id) {
        return repository.findById(id).orElseThrow(
            () -> new RuntimeException("Cliente no encontrado con ID: " + id)
        );
    }

    public Cliente save(Cliente cliente) {
        return repository.save(cliente);
    }

    public Cliente update(Integer id, Cliente clienteActualizado) {
        Cliente clienteExistente = findById(id);

        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setEmail(clienteActualizado.getEmail());
        clienteExistente.setTelefono(clienteActualizado.getTelefono());
        clienteExistente.setDireccion(clienteActualizado.getDireccion());
        clienteExistente.setIdKeycloak(clienteActualizado.getIdKeycloak());

        return repository.save(clienteExistente);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}