package com.backend.BackendJWT.Services;

import com.backend.BackendJWT.Models.Auth.*;
import com.backend.BackendJWT.Config.Jwt.JwtService;
import com.backend.BackendJWT.Models.DTO.*;
import com.backend.BackendJWT.Repositories.Auth.MedidorRepository;
import com.backend.BackendJWT.Repositories.Auth.RoleRepository;
import com.backend.BackendJWT.Repositories.Auth.ClienteRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private MedidorRepository medidorRepository;

    public AuthResponse registerConsumo(RegisterConsumoRequest request){
            return null;
    }
    public AuthResponse registerMedidor(RegisterMedidorRequest request, String token) {
        try {
            // Extraer el ID del cliente del token
            System.out.println("entra al service");
            String cleanedToken = token.replace("Bearer ", "");
            System.out.println(cleanedToken);

            Long clienteId = jwtService.getClaim(cleanedToken, claims -> claims.get("id_cliente", Long.class));
            System.out.println(clienteId);

            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            System.out.println(cliente);

            Medidor medidor = Medidor.builder()
                    .region(request.getRegion())
                    .region(request.getComuna())
                    .region(request.getDireccion())
                    .numcliente(request.getNumcliente())
                    .build();

            System.out.println(medidor);

            medidorRepository.save(medidor);

            return AuthResponse.builder()
                    .success(true)
                    .token("Medidor registrado exitosamente")
                    .build();

        } catch (Exception e) {
            System.out.println("cae en catch");
            return AuthResponse.builder()
                    .success(false)
                    .token("Error al registrar el medidor: " + e.getMessage())
                    .build();
        }
    }


    public AuthResponse login(LoginRequest request) {
        System.out.println(passwordEncoder.encode(request.getPassword()));
        try {
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

        } catch (AuthenticationException e) {
            // Manejo de errores de autenticación
            return AuthResponse.builder()
                    .success(false)
                    .token("Error de autenticación: " + e.getMessage())
                    .build();

        } catch (Exception e) {
            // Manejo de otros errores inesperados
            return AuthResponse.builder()
                    .success(false)
                    .token("Error interno del servidor: " + e.getMessage())
                    .build();
        }
    }

    public AuthResponse register(RegisterRequest request) {

        try {
            if (clienteRepository.existsByRut(request.getRut())) {
                throw new UsernameAlreadyExistsException("Rut '" + request.getRut() + "' is already registered");
            }

            if (clienteRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("Email '" + request.getEmail() + "' is already associated with an account");
            }


                // Fetch the default role
                Role defaultRole = roleRepository.findByRoleName(ERole.USER)
                        .orElseThrow(() -> new RuntimeException("Default role not found"));

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

        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            // Maneja excepciones específicas relacionadas con el registro de usuario
            throw new Error("Ha ocurrido un error "+e);
        } catch (RuntimeException e) {
            // Maneja cualquier otra excepción de tiempo de ejecución
            throw new RuntimeException("Error al registrar el usuario: " + e.getMessage(), e);
        } catch (Exception e) {
            // Maneja cualquier otra excepción
            throw new RuntimeException("Se produjo un error inesperado durante el registro: " + e.getMessage(), e);
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
