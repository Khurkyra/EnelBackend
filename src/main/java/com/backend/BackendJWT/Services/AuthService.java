package com.backend.BackendJWT.Services;

import com.backend.BackendJWT.Models.Auth.*;
import com.backend.BackendJWT.Config.Jwt.JwtService;
import com.backend.BackendJWT.Models.DTO.*;
import com.backend.BackendJWT.Repositories.Auth.RoleRepository;
import com.backend.BackendJWT.Repositories.Auth.ClienteRepository;

import com.backend.BackendJWT.Validaciones.RutValidation;
import com.backend.BackendJWT.Validaciones.StringValidation;
import com.backend.BackendJWT.Validaciones.ValidacionPorCampo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
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
            // Validar el RUT usando validacionModule11
            if (request.getRut() == null || request.getRut().isEmpty() || request.getRut().trim().isEmpty()){
                return AuthResponse.builder()
                        .success(false)
                        .token("El campo rut es obligatorio y no puede ser vacio")
                        .build();
            }
            if(request.getRut().contains(" ")){
                return AuthResponse.builder()
                        .success(false)
                        .token("El campo rut no puede tener espacios vacios")
                        .build();
            }
            ValidationResponse rutValidation = RutValidation.validacionModule11(request.getRut());
            if (!rutValidation.isSuccess()) {
                return AuthResponse.builder()
                        .success(false)
                        .token(""+rutValidation.getMessage())
                        .build();
            }

            //Valida Password
            if (request.getPassword() == null || request.getPassword().isEmpty() || request.getPassword().trim().isEmpty()) {
                return AuthResponse.builder()
                        .success(false)
                        .token("El campo password es obligatorio y no puede ser vacio")
                        .build();
            }

            //Intenta autenticar al usuario usando el RUT y la contraseña
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getRut(), request.getPassword()));
            //Busca el usuario en el repositorio usando el RUT
            UserDetails user = clienteRepository.findByRut(request.getRut())
                    .orElseThrow(() -> new AuthenticationException("Usuario no encontrado") {
                    });
            //Genera el token JWT para el usuario
            String token = jwtService.getToken(user);
            //Retorna la respuesta con el token
            return AuthResponse.builder()
                    .success(true)
                    .token(token)
                    .build();
        }
        catch (AuthenticationException e){
            return AuthResponse.builder()
                    .success(false)
                    .token("El rut o la contraseña son invalidos")
                    .build();
        }
        catch (Exception e) {
        return AuthResponse.builder()
                .success(false)
                .token("Hubo un error al intentar ingresar al home")
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
                        .token("Registro existoso. Por favor ingrese con sus credenciales.")
                        .build();

        }
        catch (Exception e) {
            // Maneja cualquier otra excepción
            return AuthResponse.builder()
                    .success(false)
                    .token("Hubo un error al registrar el usuario")
                    .build();
        }
    }


    public AuthResponse getUser(SearchUserRequest request) {
        try {
            if(request.getEmail().contains(" ")|| request.getEmail() == null || request.getEmail().isEmpty() || request.getEmail().trim().isEmpty()){
                return AuthResponse.builder()
                        .success(false)
                        .token("El campo email es obligatorio, no puede ser vacio ni contener espacios vacios.")
                        .build();
            }
            if(!ValidacionPorCampo.isValidEmail(request.getEmail())){
                return AuthResponse.builder()
                        .success(false)
                        .token("El campo email es invalido. Debe tener una longitud entre 4 y 50 caracteres, un @ y un dominio. No puede tener espacios vacios.")
                        .build();            }

                    Optional<Cliente> optionalUser = clienteRepository.findByEmail(request.getEmail());
                    if(optionalUser.isPresent()) {
                        return AuthResponse.builder()
                                .success(true)
                                .token("Email valido, se le enviará un codigo de verificación al correo")
                                .build();
                    } else {
                        return AuthResponse.builder()
                                .success(false)
                                .token("El email ingresado no existe en la base de datos")
                                .build();
                    }
        }
        catch (Exception e) {
            // Manejo de cualquier otra excepción inesperada
            return AuthResponse.builder()
                    .success(false)
                    .token("Hubo un error al intentar buscar el email ingresado: "+e.getMessage())
                    .build();
        }
    }


    public AuthResponse updatePassword(UpdatePasswordRequest request) {
        try {
            if(!ValidacionPorCampo.isValidEmail(request.getEmail())){
                return AuthResponse.builder()
                        .success(false)
                        .token("El campo email es invalido. Debe tener una longitud entre 4 y 50 caracteres, un @ y un dominio. No puede tener espacios vacios.")
                        .build();
            }

            Optional<Cliente> optionalUser = clienteRepository.findByEmail(request.getEmail());
            if(optionalUser.isPresent()){
                Cliente cliente = optionalUser.get();
                if (request.getNewPassword() == null || request.getNewPassword().isEmpty() || request.getNewPassword().trim().isEmpty()) {
                    return AuthResponse.builder()
                            .success(false)
                            .token("El campo contraseña es obligatorio y no puede ser vacio")
                            .build();
                }
                if(request.getNewPassword().contains(" ")){
                    return AuthResponse.builder()
                            .success(false)
                            .token("El campo contraseña no puede tener espacios vacios")
                            .build();
                }
                if(StringValidation.validatePassword(request.getNewPassword())){
                    cliente.setPassword(passwordEncoder.encode(request.getNewPassword()));

                    return AuthResponse.builder()
                            .success(true)
                            .token("Se ha actualizado su contraseña")
                            .build();
                }else{
                    return AuthResponse.builder()
                            .success(false)
                            .token("El campo password debe tener minimo de ocho caracteres y maximo 15, con al menos una letra mayuscula, una letra minuscula y un numero")
                            .build();
                }
            }else{
                return AuthResponse.builder()
                        .success(false)
                        .token("El email ingresado no existe en la base de datos")
                        .build();
            }
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .token("Hubo un error al intentar actualizar la contraseña: "+e.getMessage())
                    .build();
        }
    }
}
