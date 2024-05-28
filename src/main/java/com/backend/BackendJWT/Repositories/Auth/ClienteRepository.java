package com.backend.BackendJWT.Repositories.Auth;

import com.backend.BackendJWT.Models.Auth.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    boolean existsByRut(String rut);

    boolean existsByEmail(String email);

    Optional<Cliente> findByRut(String rut);

    Optional<Cliente> findById(Long id);

    Optional<Cliente> findByEmail(String email);


}
