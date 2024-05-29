package com.backend.BackendJWT.Controllers;

import com.backend.BackendJWT.Config.Jwt.JwtService;
import com.backend.BackendJWT.Models.Auth.*;
import com.backend.BackendJWT.Services.AuthService;
import com.backend.BackendJWT.Services.ClienteService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final ClienteService clienteService;
    private final JwtService jwtService;

    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request)
    {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request)
    {
        return ResponseEntity.ok(authService.register(request));
    }
    //obtiene al usuario para verificar su existencia en el sistema
    @PostMapping("/getEmail")
    public ResponseEntity<AuthResponse>searchUser(@RequestBody SearchUserRequest request){
        return ResponseEntity.ok(authService.getUser(request));
    }
    //si existe y codigos coinciden, se envia nueva contraseña a este endpoint para su actualizacion
    @PutMapping("/update-password")
    public ResponseEntity<AuthResponse> updatePassword(@RequestBody UpdatePasswordRequest request) {
        return ResponseEntity.ok(authService.updatePassword(request));
    }

    @GetMapping("/user/profile")
    public ResponseEntity<Cliente> getUserProfile(@RequestHeader("Authorization") String token) {
        // Extraer el token del encabezado "Bearer "
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String rut = jwtService.getUserIdFromToken(token);
        Cliente cliente = clienteService.getClienteByRut(rut);

        return ResponseEntity.ok(cliente);
    }

    @PatchMapping("/user/profile/update")
    public ResponseEntity<Cliente> updateUserProfile(@RequestHeader("Authorization") String token, @RequestBody UpdateClienteRequest updateClienteRequest) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String rut = jwtService.getUserIdFromToken(token);
        Cliente updatedCliente = clienteService.actualizarClienteParcial(rut, updateClienteRequest);
        return ResponseEntity.ok(updatedCliente);
    }

    @PostMapping("/user/medidores")
    public ResponseEntity<?> registrarMedidor(@RequestBody Medidor medidor, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token format");
        }

        String rut = jwtService.getUserIdFromToken(token);
        Cliente cliente = clienteService.getClienteByRut(rut);

        AuthResponse response = clienteService.registrarMedidor(medidor, cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PostMapping("/user/medidores/{medidorId}/consumos")
    public ResponseEntity<?> registrarConsumo(@PathVariable Long medidorId, @RequestBody Consumo consumo, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Puedes verificar aquí si el token es válido y si el medidor pertenece al cliente
        AuthResponse nuevoConsumo = clienteService.registrarConsumo(medidorId, consumo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoConsumo);
    }

    @DeleteMapping("/user/medidores/{medidorId}")
    public ResponseEntity<?> eliminarMedidor(@PathVariable Long medidorId, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token format");
        }

        String rut = jwtService.getUserIdFromToken(token);
        Cliente cliente = clienteService.getClienteByRut(rut);

        // Verificar si el medidor pertenece al cliente y si puede ser eliminado
        boolean eliminado = clienteService.eliminarMedidor(medidorId);

        if (eliminado) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Medidor eliminado con éxito");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede eliminar el medidor porque tiene registros de consumo");
        }
    }
    @DeleteMapping("/user")
    public ResponseEntity<?> eliminarUsuario(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token format");
        }

        String rut = jwtService.getUserIdFromToken(token);

        boolean eliminado = clienteService.eliminarUsuario(rut);

        if (eliminado) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Usuario eliminado con éxito");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede eliminar el usuario");
        }
    }



}
