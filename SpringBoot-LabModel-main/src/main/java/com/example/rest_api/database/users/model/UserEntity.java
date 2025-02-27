package com.example.rest_api.database.users.model;

import com.example.rest_api.security.AuthenticatedUser;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter @Setter @EqualsAndHashCode
@Table(name="app_user")
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "username")
    private String username;

    /* Force the validation of the email on the database level too */
    @Column(name = "email", unique = true, columnDefinition = "VARCHAR(255) CHECK (email ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')")
    private String email;

    @Column(name="isoauth_account")
    private Boolean isOAuthAccount;

    @Basic
    @Column(name = "password")
    private String password;

    @Transient
    private String repeatPassword;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "app_users_roles",
            joinColumns = @JoinColumn(
                    name = "app_user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private List<RoleEntity> roles = new ArrayList<>();

    public void addRole(RoleEntity role) {
        roles.add(role);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    public AuthenticatedUser toAuthenticatedUser(){
        /* Does set only the basic details */
        return new AuthenticatedUser(this.email, this.password, this.getAuthorities());
    }
}
