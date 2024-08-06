package com.backend.BackendJWT.Controllers;

import com.backend.BackendJWT.Models.DTO.AuthResponseListObj;
import com.backend.BackendJWT.Services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    //Entrega todos los consumos.
    @GetMapping("/medidores/getAllConsumos")
    public ResponseEntity<?> getAllConsumos(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }
        AuthResponseListObj consumosDeMedidor = adminService.obtenerTodosLosConsumos();
        return ResponseEntity.ok(consumosDeMedidor);
    }

    //Entrega todos los suministros.
    @GetMapping("/medidores/getAllSuministros")
    public ResponseEntity<?> getAllSuministros(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }
        AuthResponseListObj consumosDeMedidor = adminService.obtenerTodosLosSuministros();
        return ResponseEntity.ok(consumosDeMedidor);
    }

    //Entrega todos los suministros.
    @GetMapping("/medidores/getAllMedidoresConsumo")
    public ResponseEntity<?> getAllMedidoresConsumo(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no es valido");
        }
        AuthResponseListObj consumosDeMedidor = adminService.obtenerTodosLosConsumosConMedidores();
        return ResponseEntity.ok(consumosDeMedidor);
    }
}
