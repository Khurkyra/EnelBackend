package com.backend.BackendJWT.Models.Auth;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "suministro")
public class Suministro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 4, max = 20)
    @Column(nullable = false, length = 30)
    private String numcliente;

    @NotNull
    @Size(min = 2, max = 30)
    @Column(nullable = false, length = 30)
    private String motivo;

    @NotNull
    @Size(min = 4, max = 90)
    @Column(nullable = false, length = 60)
    private String comentario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "medidor_id")
    @NotNull
    @JsonBackReference
    private Medidor medidor;  // Relación con la entidad Medidor

}
