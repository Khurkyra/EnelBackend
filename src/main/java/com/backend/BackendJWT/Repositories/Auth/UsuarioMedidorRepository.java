package com.backend.BackendJWT.Repositories.Auth;

import com.backend.BackendJWT.Models.Auth.UsuarioMedidor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.BackendJWT.Models.Auth.Cliente;
import com.backend.BackendJWT.Models.Auth.Medidor;

import java.util.List;

public interface UsuarioMedidorRepository extends JpaRepository<UsuarioMedidor, Long> {
    List<UsuarioMedidor> findByClienteId(Long clienteId);
    boolean existsByClienteAndMedidor(Cliente cliente, Medidor medidor);

    UsuarioMedidor findByClienteAndMedidor(Cliente cliente, Medidor medidor);

    @Transactional
    void deleteByMedidorAndClienteRut(Medidor medidor, String rut);
}
