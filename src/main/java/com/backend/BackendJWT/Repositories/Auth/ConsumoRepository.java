package com.backend.BackendJWT.Repositories.Auth;

import com.backend.BackendJWT.Models.Auth.Consumo;
import com.backend.BackendJWT.Models.Auth.Medidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ConsumoRepository extends JpaRepository<Consumo, Long> {
    // Métodos de consulta personalizados si es necesario
    List<Consumo> findByMedidorId(Long medidorId);
}