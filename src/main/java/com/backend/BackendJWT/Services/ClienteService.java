package com.backend.BackendJWT.Services;

import com.backend.BackendJWT.Config.Jwt.JwtService;
import com.backend.BackendJWT.Models.Auth.*;
import com.backend.BackendJWT.Models.DTO.*;
import com.backend.BackendJWT.Repositories.Auth.ClienteRepository;
import com.backend.BackendJWT.Repositories.Auth.ConsumoRepository;
import com.backend.BackendJWT.Repositories.Auth.MedidorRepository;
import com.backend.BackendJWT.Repositories.Auth.SuministroRepository;
import com.backend.BackendJWT.Validaciones.StringValidation;
import com.backend.BackendJWT.Validaciones.ValidacionPorCampo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private SuministroRepository suministroRepository;

    public Cliente getClienteByRut(String rut) {
        System.out.println("cliente rut: "+rut);
        return clienteRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Autowired
    private MedidorRepository medidorRepository;


    public AuthResponse registrarMedidor(RegisterMedidorRequest medidorRequest, Cliente cliente) {
        try {
            ValidationResponse validacionPorCampo = ValidacionPorCampo.validacionPorCampoMedidor(medidorRequest);
            if (!validacionPorCampo.isSuccess()) {
                return AuthResponse.builder()
                        .success(false)
                        .token(""+validacionPorCampo.getMessage())
                        .build();
            }
            Cliente persistedCliente = clienteRepository.save(cliente); // Persistir el cliente si aún no lo está

            Medidor medidor = Medidor.builder()
                    .region(medidorRequest.getRegion())
                    .comuna(medidorRequest.getComuna())
                    .direccion(medidorRequest.getDireccion())
                    .numcliente(medidorRequest.getNumcliente())
                    .cliente(persistedCliente)
                    .build();

           medidorRepository.save(medidor);
            return AuthResponse.builder()
                    .success(true)
                    .token("Medidor registrado exitosamente")
                    .build();
        }
        catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .token("Hubo un error al intentar registrar el medidor")
                    .build();
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
    public Cliente registrarSuministro(Long medidorId, Suministro suministro){
        Medidor medidor = medidorRepository.findById(medidorId)
                .orElseThrow(() -> new RuntimeException("Medidor not found"));
        suministro.setMedidor(medidor);
        suministroRepository.save(suministro);
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
