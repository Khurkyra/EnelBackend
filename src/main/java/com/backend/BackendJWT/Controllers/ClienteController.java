package com.backend.BackendJWT.Controllers;

import com.backend.BackendJWT.Config.Jwt.JwtService;
import com.backend.BackendJWT.Models.Auth.*;
import com.backend.BackendJWT.Models.DTO.*;
import com.backend.BackendJWT.Repositories.Auth.ConsumoRepository;
import com.backend.BackendJWT.Repositories.Auth.MedidorRepository;
import com.backend.BackendJWT.Repositories.Auth.UsuarioMedidorRepository;
import com.backend.BackendJWT.Services.ClienteService;
import com.backend.BackendJWT.Validaciones.RutValidation;
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
    private final MedidorRepository medidorRepository;
    private final UsuarioMedidorRepository usuarioMedidorRepository;
    private final ConsumoRepository consumoRepository;


    //Datos del cliente
    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        // Extraer el token del encabezado "Bearer "
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es válido");
        }
        String rut = jwtService.getUserIdFromToken(token);
        AuthResponseObj cliente = clienteService.obtenerCliente(rut);
        return ResponseEntity.ok(cliente);
    }


    //Listado de medidores asociados al cliente
    @GetMapping("/userMedidores/profile")
    public ResponseEntity<?> getUserMedidoresProfile(@RequestHeader("Authorization") String token) {
        // Extraer el token del encabezado "Bearer "
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es válido");
        }
        String rut = jwtService.getUserIdFromToken(token);
        AuthResponseListObj medidoresDeCliente = clienteService.obtenerMedidoresDeCliente(rut);
        return ResponseEntity.ok(medidoresDeCliente);
    }



    //Entrega consumos del medidor.
    @GetMapping("/medidores/{medidorId}/getConsumos")
    public ResponseEntity<?> getConsumos(@PathVariable Long medidorId, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }
        AuthResponseListObj consumosDeMedidor = clienteService.obtenerConsumosDeMedidor(medidorId);
        return ResponseEntity.ok(consumosDeMedidor);
    }


    //Entrega suministros del medidor.
    @GetMapping("/medidores/{medidorId}/getSuministros")
    public ResponseEntity<?> getSuministros(@PathVariable Long medidorId, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }
        AuthResponseListObj suministrosDeMedidor = clienteService.obtenerSuministrosDeMedidor(medidorId);
        return ResponseEntity.ok(suministrosDeMedidor);
    }

    //Rutificador
    @PostMapping("/rut")
    public ResponseEntity<?> verificarRut(@RequestBody RutRequest request, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }
        ValidationResponse rutvalidation = RutValidation.validacionModule11(request.getRut());
        if(!rutvalidation.isSuccess()){
            //Cliente cliente = clienteService.getClienteByRut(rut);
            String response = "rut malo";
            return ResponseEntity.ok(response + rutvalidation.getMessage());
        }
        //Cliente cliente = clienteService.getClienteByRut(rut);
        String response = "rut bueno";
        return ResponseEntity.ok(response);

    }

    //Entrega fecha de consumo para registrar consumo del medidor.
    @GetMapping("/medidores/{medidorId}/getFechaConsumo")
    public ResponseEntity<?> getFechaConsumo(@PathVariable Long medidorId, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }
        GetFechaResponse fechaConsumo = clienteService.obtenerFechaConsumo(medidorId);
        return ResponseEntity.ok(fechaConsumo);
    }


    //Crear medidor
    @PostMapping("/medidores")
    public ResponseEntity<?> registrarMedidor(@RequestBody RegisterMedidorRequest medidorRequest, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }
        String rut = jwtService.getUserIdFromToken(token);
        //Cliente cliente = clienteService.getClienteByRut(rut);
        return ResponseEntity.ok(clienteService.registrarMedidor(medidorRequest, rut));

    }


    //Crear consumo de medidor
    @PostMapping("/medidores/{medidorId}/consumos")
    public ResponseEntity<?> registrarConsumo(@PathVariable Long medidorId, @RequestBody Consumo consumo, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }

        AuthResponse nuevoConsumo = clienteService.registrarConsumo(medidorId, consumo);// Obtener los datos actualizados del cliente
        return ResponseEntity.ok(nuevoConsumo);
    }


    //Crear consumo de medidor
    @PostMapping("/medidores/{medidorId}/suministro")
    public ResponseEntity<?> registrarSuministro(@PathVariable Long medidorId, @RequestBody Suministro suministro, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }

        AuthResponse nuevoSuministro = clienteService.registrarSuministro(medidorId, suministro);// Obtener los datos actualizados del cliente
        return ResponseEntity.ok(nuevoSuministro);
    }



    //Actualizar cliente
    @PatchMapping("/profile/update")
    public ResponseEntity<?> updateUserProfile(@RequestHeader("Authorization") String token, @RequestBody UpdateClienteRequest updateClienteRequest) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }
        String rut = jwtService.getUserIdFromToken(token);
        AuthResponse updatedCliente = clienteService.actualizarClienteParcial(rut, updateClienteRequest);
        return ResponseEntity.ok(updatedCliente);
    }



    //Borrar cliente
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



    @DeleteMapping("/medidores/{medidorId}")
    public ResponseEntity<?> eliminarMedidor(@PathVariable Long medidorId, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }

        String rut = jwtService.getUserIdFromToken(token);

        AuthResponse updatedCliente = clienteService.eliminarAsociacionMedidorYObtenerClienteActualizado(medidorId, rut); // Obtener los datos actualizados del cliente
        return ResponseEntity.ok(updatedCliente);

    }


}
