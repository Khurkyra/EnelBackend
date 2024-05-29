package com.backend.BackendJWT.Models.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    String rut;
    String password;
    String firstname;
    String lastname;
    String email;
    String phoneNumber;
}
