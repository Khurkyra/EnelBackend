package com.backend.BackendJWT.Services;

import com.backend.BackendJWT.Config.Jwt.JwtService;
import com.backend.BackendJWT.Models.Auth.*;
import com.backend.BackendJWT.Models.DTO.AuthResponse;
import com.backend.BackendJWT.Models.DTO.UpdateClienteRequest;
import com.backend.BackendJWT.Models.DTO.UpdatePasswordRequest;
import com.backend.BackendJWT.Repositories.Auth.ClienteRepository;
import com.backend.BackendJWT.Repositories.Auth.ConsumoRepository;
import com.backend.BackendJWT.Repositories.Auth.MedidorRepository;
import com.backend.BackendJWT.Validaciones.StringValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
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

    public ResponseEntity<?> registrarMedidor(Medidor medidor, Cliente cliente) {
        try {
            if (medidor.getComuna() == null || medidor.getComuna().isEmpty() || medidor.getComuna().trim().isEmpty()) {
                throw new IllegalArgumentException("la comuna es obligatoria");
            }
            if (medidor.getRegion() == null || medidor.getRegion().isEmpty() || medidor.getRegion().trim().isEmpty()) {
                throw new IllegalArgumentException("la region es obligatoria");
            }
            if (medidor.getDireccion() == null || medidor.getDireccion().isEmpty() || medidor.getDireccion().trim().isEmpty()) {
                throw new IllegalArgumentException("la direccion es obligatoria");
            }
            if (medidor.getNumcliente() == null || medidor.getNumcliente().isEmpty() || medidor.getNumcliente().trim().isEmpty()) {
                throw new IllegalArgumentException("el numero de cliente es obligatorio");
            }
            medidor.setCliente(cliente);
            medidorRepository.save(medidor);
            return ResponseEntity.ok(cliente.getRut()); // Devolver el cliente actualizado

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + e.getMessage());
        }
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

        if (updateClienteRequest.getPassword() != null && !updateClienteRequest.getPassword().isEmpty() && !updateClienteRequest.getPassword().trim().isEmpty()) {
            cliente.setPassword(passwordEncoder.encode(updateClienteRequest.getPassword()));
        }
        if (updateClienteRequest.getEmail() != null && !updateClienteRequest.getEmail().isEmpty() && !updateClienteRequest.getEmail().trim().isEmpty()) {
            cliente.setEmail(updateClienteRequest.getEmail());
        }
        if (updateClienteRequest.getPhoneNumber() != null && !updateClienteRequest.getPhoneNumber().isEmpty() && !updateClienteRequest.getPhoneNumber().trim().isEmpty()) {
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
