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

    @Size(min = 7, max = 20)
    @Column(nullable = false, length = 30)
    private String rut;

    @NotNull
    @Size(min = 4, max = 12)
    @Column(nullable = false)
    private String password;

    @NotNull
    @Size(min = 4, max = 30)
    @Column(nullable = false, length = 30)
    private String firstname;

    @NotNull
    @Size(min = 4, max = 30)
    @Column(nullable = false, length = 30)
    private String lastname;

    @NotNull
    @Email
    @Size(min = 4, max = 255)
    @Column(nullable = false, length = 255)
    private String email;


    @NotNull
    @Size(min = 8, max = 12)
    @Column(nullable = false, length = 12)
    private String phoneNumber;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "role_id")
    @NotNull
    private Role role;  // Ensure there is no @Enumerated here as Role is an entity

    @OneToMany(mappedBy = "cliente")
    @JsonManagedReference
    private List<Medidor> medidores;

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
                ", medidores=" + (medidores != null ? medidores.size() + " medidores" : "0 medidores") +
                '}';
    }
}
