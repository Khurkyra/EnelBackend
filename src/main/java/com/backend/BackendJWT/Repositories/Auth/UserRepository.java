package com.backend.BackendJWT.Repositories.Auth;

import com.backend.BackendJWT.Models.Auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByRut(String rut);

    boolean existsByEmail(String email);

    Optional<User> findByRut(String rut);
}
