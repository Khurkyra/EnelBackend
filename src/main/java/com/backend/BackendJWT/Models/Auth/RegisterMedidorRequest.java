package com.backend.BackendJWT.Models.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterMedidorRequest {
    private String nombre;
    private String numcliente;
}
