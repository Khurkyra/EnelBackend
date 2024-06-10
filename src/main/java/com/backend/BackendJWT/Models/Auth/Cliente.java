package com.backend.BackendJWT.Models.Auth;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "cliente")
public class Cliente implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 7, max = 10)
    @Column(nullable = false, length = 10)
    private String rut;

    @NotNull
    @Size(min = 8, max = 15)
    @Column(nullable = false)
    private String password;

    @NotNull
    @Size(min = 2, max = 20)
    @Column(nullable = false, length = 40)
    private String firstname;

    @NotNull
    @Size(min = 2, max = 20)
    @Column(nullable = false, length = 40)
    private String lastname;

    @NotNull
    @Email
    @Size(min = 4, max = 50)
    @Column(nullable = false, length = 50)
    private String email;


    @NotNull
    @Size(min = 8, max = 9)
    @Column(nullable = false, length = 9)
    private String phoneNumber;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "role_id")
    @NotNull
    private Role role;  // Ensure there is no @Enumerated here as Role is an entity

    @OneToMany(mappedBy = "cliente")
    @JsonManagedReference
    private List<UsuarioMedidor> usuarioMedidores;

    @Override
    public String getUsername() {
        // Puedes devolver el RUT, email, o cualquier otro identificador único
        return rut;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getRoleName().name()));
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", rut='" + rut + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role=" + role +
                ", usuarioMedidores=" + (usuarioMedidores != null ? usuarioMedidores.size() + " usuarioMedidores" : "0 usuarioMedidores") +
                '}';
    }
}
