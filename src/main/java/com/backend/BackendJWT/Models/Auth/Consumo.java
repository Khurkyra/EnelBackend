package com.backend.BackendJWT.Models.Auth;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date fecha;

    @NotNull
    @Column(nullable = false)
    @Min(0)
    @Max(999999)
    private Integer lectura;


    @NotNull
    @Column(nullable = false)
    @Min(0)
    @Max(999999)
    private Integer consumo;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer costoEnergia;


    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer subtotal;


    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer iva;


    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer total;


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
