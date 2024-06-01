package com.backend.BackendJWT.Controllers;

import com.backend.BackendJWT.Config.Jwt.JwtService;
import com.backend.BackendJWT.Models.Auth.Cliente;
import com.backend.BackendJWT.Models.Auth.Consumo;
import com.backend.BackendJWT.Models.Auth.Medidor;
import com.backend.BackendJWT.Models.DTO.UpdateClienteRequest;
import com.backend.BackendJWT.Services.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("cliente")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService clienteService;
    private final JwtService jwtService;


    //datos del cliente
    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        // Extraer el token del encabezado "Bearer "
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es válido");
        }
        String rut = jwtService.getUserIdFromToken(token);
        Cliente cliente = clienteService.getClienteByRut(rut);

        return ResponseEntity.ok(cliente);
    }

    //actualizar cliente
    @PatchMapping("/profile/update")
    public ResponseEntity<?> updateUserProfile(@RequestHeader("Authorization") String token, @RequestBody UpdateClienteRequest updateClienteRequest) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }
        String rut = jwtService.getUserIdFromToken(token);
        Cliente updatedCliente = clienteService.actualizarClienteParcial(rut, updateClienteRequest);
        return ResponseEntity.ok(updatedCliente);
    }


    //crear medidor
    @PostMapping("/medidores")
    public ResponseEntity<?> registrarMedidor(@RequestBody Medidor medidor, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }

        String rut = jwtService.getUserIdFromToken(token);
        Cliente cliente = clienteService.getClienteByRut(rut);

        Cliente updatedCliente = clienteService.registrarMedidor(medidor, cliente); // Obtener los datos actualizados del cliente
        return ResponseEntity.ok(updatedCliente);
    }


    //crear consumo de medidor
    @PostMapping("/medidores/{medidorId}/consumos")
    public ResponseEntity<?> registrarConsumo(@PathVariable Long medidorId, @RequestBody Consumo consumo, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }

        Cliente updatedCliente = clienteService.registrarConsumo(medidorId, consumo); // Obtener los datos actualizados del cliente
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCliente);
    }


    //borrar medidor
    @DeleteMapping("/medidores/{medidorId}")
    public ResponseEntity<?> eliminarMedidor(@PathVariable Long medidorId, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }

        String rut = jwtService.getUserIdFromToken(token);

        try {
            Cliente updatedCliente = clienteService.eliminarMedidorYObtenerClienteActualizado(medidorId, rut); // Obtener los datos actualizados del cliente
            return ResponseEntity.ok(updatedCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede eliminar el medidor ya que posee registros de consumo asociados");
        }
    }

    //borrar cliente
    @DeleteMapping("/deleteAccount")
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
