package com.backend.BackendJWT.Models.DTO;
import com.backend.BackendJWT.Models.Auth.Consumo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsumoConComuna{
    private Consumo consumo;
    private String comuna;
    private String region;
}
