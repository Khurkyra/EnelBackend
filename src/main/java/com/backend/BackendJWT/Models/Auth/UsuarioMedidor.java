package com.backend.BackendJWT.Models.Auth;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "usuario_medidor")
public class UsuarioMedidor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    @JsonBackReference // Indica el lado que no es el propietario de la relación
    @JsonIgnore
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medidor_id")
    @JsonBackReference
    // Indica el lado que no es el propietario de la relación
    //@JsonIgnore
    private Medidor medidor;
}
