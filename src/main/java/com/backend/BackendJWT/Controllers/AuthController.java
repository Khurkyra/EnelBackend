package com.backend.BackendJWT.Controllers;

import com.backend.BackendJWT.Config.Jwt.JwtService;
import com.backend.BackendJWT.Models.DTO.*;
import com.backend.BackendJWT.Services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
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


}
