package com.backend.BackendJWT.Controllers;

import com.backend.BackendJWT.Config.Jwt.JwtService;
import com.backend.BackendJWT.Models.DTO.*;
import com.backend.BackendJWT.Services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request)
    {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/loginAdmin")
    public ResponseEntity<AuthResponse> loginAdmin(@RequestBody LoginRequest request)
    {
        return ResponseEntity.ok(authService.loginAdmin(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerCliente(@RequestBody RegisterRequest request)
    {
        return ResponseEntity.ok(authService.registerCliente(request));
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<AuthResponse> registerAdmin(@RequestBody RegisterRequest request)
    {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    //obtiene al usuario para verificar su existencia en el sistema
    @PostMapping("/getEmail")
    public ResponseEntity<AuthResponse>searchUser(@RequestBody SearchUserRequest request){
        return ResponseEntity.ok(authService.getUser(request));
    }

    //si existe y codigos coinciden, se envia nueva contraseña a este endpoint para su actualizacion
    @PutMapping("/update-password")
    public ResponseEntity<AuthResponse> updatePassword(@RequestBody UpdatePasswordRequest request ) {
        return ResponseEntity.ok(authService.updatePassword(request));
    }
}
