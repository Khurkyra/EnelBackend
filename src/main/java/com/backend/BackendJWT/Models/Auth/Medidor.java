package com.backend.BackendJWT.Models.Auth;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "medidor")
public class Medidor{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 7, max = 20)
    @Column(nullable = false, length = 30)
    private String nombre;

    @NotNull
    @Size(min = 7, max = 20)
    @Column(nullable = false, length = 30)
    private String numcliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "cliente_id")
    @NotNull
    @JsonBackReference
    private Cliente cliente;  // Relación con la entidad Cliente

    @OneToMany(mappedBy = "medidor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Consumo> consumos;  // Relación con la entidad Consumo

}
