package com.backend.BackendJWT.Services;

import com.backend.BackendJWT.Models.Auth.*;
import com.backend.BackendJWT.Config.Jwt.JwtService;
import com.backend.BackendJWT.Models.DTO.*;
import com.backend.BackendJWT.Repositories.Auth.MedidorRepository;
import com.backend.BackendJWT.Repositories.Auth.RoleRepository;
import com.backend.BackendJWT.Repositories.Auth.ClienteRepository;

import com.backend.BackendJWT.Validaciones.RutValidation;
import com.backend.BackendJWT.Validaciones.ValidacionPorCampo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository;

    public AuthResponse login(LoginRequest request) {
        try {
            if (request.getRut() == null || request.getRut().isEmpty() || request.getRut().trim().isEmpty()){
                return AuthResponse.builder()
                        .success(false)
                        .token("El campo rut no puede ser vacio")
                        .build();
            }
            // Validar el RUT usando validacionModule11
            ValidationResponse rutValidation = RutValidation.validacionModule11(request.getRut());

            if (!rutValidation.isSuccess()) {
                return AuthResponse.builder()
                        .success(false)
                        .token(""+rutValidation.getMessage())
                        .build();
            }
            if (request.getPassword() == null || request.getPassword().isEmpty() || request.getPassword().trim().isEmpty()) {
                return AuthResponse.builder()
                        .success(false)
                        .token("El campo password no puede ser vacio")
                        .build();
            }

            // Intenta autenticar al usuario usando el RUT y la contraseña
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getRut(), request.getPassword()));

            // Busca el usuario en el repositorio usando el RUT
            UserDetails user = clienteRepository.findByRut(request.getRut())
                    .orElseThrow(() -> new AuthenticationException("Usuario no encontrado") {
                    });

            // Genera el token JWT para el usuario
            String token = jwtService.getToken(user);

            // Retorna la respuesta con el token
            return AuthResponse.builder()
                    .success(true)
                    .token(token)
                    .build();


        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .token("Hubo un problema en el servidor")
                    .build();
        }
    }




    public AuthResponse register(RegisterRequest request) {

        try {
            if (clienteRepository.existsByRut(request.getRut())) {
                return AuthResponse.builder()
                        .success(false)
                        .token("El rut ya esta registrado en la base de datos")
                        .build();
            }
            if (clienteRepository.existsByEmail(request.getEmail())) {
                return AuthResponse.builder()
                        .success(false)
                        .token("El email ya existe en la base de datos")
                        .build();
            }
            // Validar el RUT usando validacionModule11
            ValidationResponse validacionPorCampo = ValidacionPorCampo.validacionPorCampo(request);
            if (!validacionPorCampo.isSuccess()) {
                return AuthResponse.builder()
                        .success(false)
                        .token(""+validacionPorCampo.getMessage())
                        .build();
            }
                // Fetch the default role
                Role defaultRole = roleRepository.findByRoleName(ERole.USER)
                        .orElseThrow(() -> new Exception("Default role not found"));

                Cliente cliente = Cliente.builder()
                        .rut(request.getRut())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .firstname(request.getFirstname())
                        .lastname(request.getLastname())
                        .email(request.getEmail())
                        .phoneNumber(request.getPhoneNumber())
                        .role(defaultRole)  // Set the fetched role
                        .build();

                clienteRepository.save(cliente);  // Persist the new user with the role in the database.

                // Generate token and return response
                return AuthResponse.builder()
                        .success(true)
                        .token(jwtService.getToken(cliente))
                        .build();

        } catch (Exception e) {
            // Maneja cualquier otra excepción
            return AuthResponse.builder()
                    .success(false)
                    .token("Hubo un error al registrar el usuario")
                    .build();
        }
    }




    public AuthResponse getUser(SearchUserRequest request) {
        try {
            boolean emailExists = clienteRepository.existsByEmail(request.getEmail());

            if (emailExists) {
                return AuthResponse.builder()
                        .success(true)
                        .token("Se le enviará un codigo de verifiación")
                        .build();
            } else {
                return AuthResponse.builder()
                        .success(false)
                        .token("No existe")
                        .build();
            }
        } catch (Exception e) {
            // Manejo de cualquier otra excepción inesperada
            throw new RuntimeException("Error interno del servidor hola: " + e.getMessage());
        }
    }



    public AuthResponse updatePassword(UpdatePasswordRequest request) {
        try {
            // Buscar el usuario por su ID y lanzar excepción si no se encuentra
            Optional<Cliente> optionalUser = clienteRepository.findByEmail(request.getEmail());
            Cliente user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            // Actualizar la contraseña del usuario
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            clienteRepository.save(user);

            return new AuthResponse(true,"Su contraseña ha sido actualizada");
        } catch (UsernameNotFoundException e) {
            // Manejo de excepción específica si el usuario no es encontrado
            throw new RuntimeException("Error de autenticación "+e.getMessage());
        } catch (Exception e) {
            // Manejo de otras excepciones inesperadas
            throw new RuntimeException("Error interno del servidor: " + e.getMessage());
        }
    }


    // Custom exception classes (create separate files for these)
    public class UsernameAlreadyExistsException extends RuntimeException {
        public UsernameAlreadyExistsException(String message) {
            super(message);
        }
    }

    public class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }
}
