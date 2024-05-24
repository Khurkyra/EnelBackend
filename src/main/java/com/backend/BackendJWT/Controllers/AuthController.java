package com.backend.BackendJWT.Controllers;

import com.backend.BackendJWT.Models.Auth.*;
import com.backend.BackendJWT.Services.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
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
    @GetMapping("update-password")
    public ResponseEntity<AuthResponse>searchUser(@RequestBody SearchUserRequest request){
        return ResponseEntity.ok(authService.getUser(request));
    }

    @PutMapping("/update-password")
    public ResponseEntity<AuthResponse> updatePassword(@RequestBody UpdatePasswordRequest request) {
        return ResponseEntity.ok(authService.updatePassword(request));
    }
}
