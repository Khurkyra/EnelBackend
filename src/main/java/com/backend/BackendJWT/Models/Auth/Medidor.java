package com.backend.BackendJWT.Models.Auth;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
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
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String region;

    @NotNull
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String comuna;

    @NotNull
    @Size(min = 2, max = 150)
    @Column(nullable = false, length = 150)
    private String direccion;

    @NotNull
    @Size(min = 2, max = 20)
    @Column(nullable = false, length = 20)
    private String numcliente;


    @NotNull
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date fecha;


    @NotNull
    @Size(min = 2, max = 20)
    @Column(nullable = false, length = 20)
    private String tipoTarifa;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer tarifa;


    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer cargoFijo;


    @OneToMany(mappedBy = "medidor")
    @JsonManagedReference
    @JsonIgnore
    private List<Consumo> consumos;  // Relación con la entidad Consumo

    @OneToMany(mappedBy = "medidor") //fetch lazy carga datos relacionados solo cuando se accede a ellos explicitamente
    @JsonManagedReference
    @JsonIgnore
    private List<Suministro> suministros;  // Relación con la entidad Consumo


    @OneToMany(mappedBy = "medidor") //fetch lazy carga datos relacionados solo cuando se accede a ellos explicitamente
    @JsonManagedReference
    @JsonIgnore
    private List<UsuarioMedidor> usuarioMedidores;


    @Override
    public String toString() {
        return "Medidor{" +
                "id=" + id +
                ", region='" + region + '\'' +
                ", comuna='" + comuna + '\'' +
                ", direccion='" + direccion + '\'' +
                ", numcliente='" + numcliente + '\'' +
                ", fecha='" + fecha + '\'' +
                ", consumos=" + (consumos != null ? consumos.size() + " consumos" : "0 consumos") +
                ", suministros=" + (suministros != null ? suministros.size() + " suministros" : "0 suministros") +
                ", usuarioMedidores=" + (usuarioMedidores != null ? usuarioMedidores.size() + " usuarioMedidores" : "0 usuarioMedidores") +
                '}';
    }
}
