package com.backend.BackendJWT.Models.Auth;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "consumo")
public class Consumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String lectura;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "medidor_id")
    @NotNull
    @JsonBackReference
    private Medidor medidor;  // Relación con la entidad Medidor

    @Override
    public String toString() {
        return "Consumo{" +
                "id=" + id +
                ", lectura='" + lectura + '\'' +
                '}';
    }

}
