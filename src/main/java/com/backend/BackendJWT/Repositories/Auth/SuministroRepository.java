package com.backend.BackendJWT.Repositories.Auth;

import com.backend.BackendJWT.Models.Auth.Consumo;
import com.backend.BackendJWT.Models.Auth.Medidor;
import com.backend.BackendJWT.Models.Auth.Suministro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuministroRepository extends JpaRepository<Suministro, Long> {
    // Métodos de consulta personalizados si es necesario
    boolean existsByMedidor(Medidor medidor);
    List<Suministro> findByMedidorId(Long medidorId);
}
