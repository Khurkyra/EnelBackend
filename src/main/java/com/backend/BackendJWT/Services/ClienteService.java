package com.backend.BackendJWT.Services;

import com.backend.BackendJWT.Models.Auth.*;
import com.backend.BackendJWT.Repositories.Auth.ClienteRepository;
import com.backend.BackendJWT.Repositories.Auth.ConsumoRepository;
import com.backend.BackendJWT.Repositories.Auth.MedidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente getClienteByRut(String rut) {
        System.out.println("cliente rut: "+rut);
        return clienteRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Autowired
    private MedidorRepository medidorRepository;

    public AuthResponse registrarMedidor(Medidor medidor, Cliente cliente) {
        medidor.setCliente(cliente);
        medidorRepository.save(medidor);
        return new AuthResponse(true, "Medidor registrado con exito");
    }

    @Autowired
    private ConsumoRepository consumoRepository;

    public AuthResponse registrarConsumo(Long medidorId, Consumo consumo) {
        Medidor medidor = medidorRepository.findById(medidorId)
                .orElseThrow(() -> new RuntimeException("Medidor not found"));
        consumo.setMedidor(medidor);
        consumoRepository.save(consumo);
        return new AuthResponse(true, "Consumo registrado con exito");
    }


    public Cliente actualizarClienteParcial(String rut, UpdateClienteRequest updateClienteRequest) {
        Cliente cliente = getClienteByRut(rut);

        if (updateClienteRequest.getPassword()!= null) {
            cliente.setFirstname(updateClienteRequest.getPassword());
        }
        if (updateClienteRequest.getEmail() != null) {
            cliente.setLastname(updateClienteRequest.getEmail());
        }
        if (updateClienteRequest.getPhoneNumber() != null) {
            cliente.setPhoneNumber(updateClienteRequest.getPhoneNumber());
        }
        return clienteRepository.save(cliente);
    }

    public boolean eliminarMedidor(Long medidorId) {
        Medidor medidor = medidorRepository.findById(medidorId)
                .orElseThrow(() -> new RuntimeException("Medidor not found"));

        if (consumoRepository.existsByMedidor(medidor)) {
            return false; // No se puede eliminar el medidor porque tiene registros de consumo
        }

        medidorRepository.delete(medidor);
        return true; // Medidor eliminado con éxito
    }

    public boolean eliminarUsuario(String rut) {
        Cliente cliente = getClienteByRut(rut);

        clienteRepository.delete(cliente);
        return true; // Usuario eliminado con éxito
    }

}
