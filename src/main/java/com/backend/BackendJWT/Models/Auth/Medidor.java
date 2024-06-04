package com.backend.BackendJWT.Models.Auth;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @Size(min = 4, max = 30)
    @Column(nullable = false, length = 30)
    private String region;

    @NotNull
    @Size(min = 4, max = 30)
    @Column(nullable = false, length = 30)
    private String comuna;

    @NotNull
    @Size(min = 4, max = 60)
    @Column(nullable = false, length = 60)
    private String direccion;

    @NotNull
    @Size(min = 2, max = 20)
    @Column(nullable = false, length = 20)
    private String numcliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "cliente_id")
    @NotNull
    @JsonBackReference
    private Cliente cliente;  // Relación con la entidad Cliente

    @OneToMany(mappedBy = "medidor")
    @JsonManagedReference
    private List<Consumo> consumos;  // Relación con la entidad Consumo

    @OneToMany(mappedBy = "medidor")
    @JsonManagedReference
    private List<Suministro> suministros;  // Relación con la entidad Consumo

    @Override
    public String toString() {
        return "Medidor{" +
                "id=" + id +
                ", region='" + region + '\'' +
                ", comuna='" + comuna + '\'' +
                ", direccion='" + direccion + '\'' +
                ", numcliente='" + numcliente + '\'' +
                ", consumos=" + (consumos != null ? consumos.size() + " consumos" : "0 consumos") +
                ", consumos=" + (suministros != null ? suministros.size() + " suministros" : "0 suministros") +
                '}';
    }
}
