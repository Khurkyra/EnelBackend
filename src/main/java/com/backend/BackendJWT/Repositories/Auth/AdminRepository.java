package com.backend.BackendJWT.Repositories.Auth;

import com.backend.BackendJWT.Models.Auth.Admin;
import com.backend.BackendJWT.Models.Auth.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    boolean existsByRut(String rut);
    boolean existsByEmail(String email);
    Optional<Admin> findByRut(String rut);

}
