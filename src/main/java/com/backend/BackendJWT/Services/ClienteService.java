package com.backend.BackendJWT.Services;

import com.backend.BackendJWT.Config.Jwt.JwtService;
import com.backend.BackendJWT.Models.Auth.*;
import com.backend.BackendJWT.Models.DTO.UpdateClienteRequest;
import com.backend.BackendJWT.Repositories.Auth.ClienteRepository;
import com.backend.BackendJWT.Repositories.Auth.ConsumoRepository;
import com.backend.BackendJWT.Repositories.Auth.MedidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    public Cliente getClienteByRut(String rut) {
        System.out.println("cliente rut: "+rut);
        return clienteRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Autowired
    private MedidorRepository medidorRepository;

    public Cliente registrarMedidor(Medidor medidor, Cliente cliente) {
        medidor.setCliente(cliente);
        medidorRepository.save(medidor);
        return getClienteByRut(cliente.getRut()); // Devolver el cliente actualizado
    }

    @Autowired
    private ConsumoRepository consumoRepository;

    public Cliente registrarConsumo(Long medidorId, Consumo consumo) {
        Medidor medidor = medidorRepository.findById(medidorId)
                .orElseThrow(() -> new RuntimeException("Medidor not found"));
        consumo.setMedidor(medidor);
        consumoRepository.save(consumo);
        return getClienteByRut(medidor.getCliente().getRut()); // Devolver el cliente actualizado
    }



    public Cliente actualizarClienteParcial(String rut, UpdateClienteRequest updateClienteRequest) {
        Cliente cliente = clienteRepository.getClienteByRut(rut);
        System.out.println("cliente: "+ cliente.toString());

        if (updateClienteRequest.getPassword()!= null) {
            cliente.setPassword(passwordEncoder.encode(updateClienteRequest.getPassword()));
        }
        if (updateClienteRequest.getEmail() != null) {
            cliente.setEmail(updateClienteRequest.getEmail());
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
    public Cliente eliminarMedidorYObtenerClienteActualizado(Long medidorId, String rut) {
        boolean eliminado = eliminarMedidor(medidorId);
        if (eliminado) {
            return getClienteByRut(rut); // Devolver el cliente actualizado si el medidor fue eliminado
        }
        throw new RuntimeException("No se puede eliminar el medidor porque tiene registros de consumo");
    }

    public boolean eliminarUsuario(String rut) {
        Cliente cliente = getClienteByRut(rut);

        clienteRepository.delete(cliente);
        return true; // Usuario eliminado con éxito
    }

}
