package com.backend.BackendJWT.Repositories.Auth;

import com.backend.BackendJWT.Models.Auth.Cliente;
import com.backend.BackendJWT.Models.Auth.Medidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedidorRepository extends JpaRepository<Medidor, Long> {
    // Métodos de consulta personalizados si es necesario
}