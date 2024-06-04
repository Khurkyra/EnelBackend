package com.backend.BackendJWT.Repositories.Auth;

import com.backend.BackendJWT.Models.Auth.Medidor;
import com.backend.BackendJWT.Models.Auth.Suministro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuministroRepository extends JpaRepository<Suministro, Long> {
    // Métodos de consulta personalizados si es necesario
    boolean existsByMedidor(Medidor medidor);
}
