package com.mrbeans.circulosestudiobackend.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
public class CustomUserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final String name;
    private final String imageUrl;
    private final String roleName;
    private final boolean isActive;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserPrincipal(UUID id,
                               String email,
                               String password,
                               String name,
                               String imageUrl,
                               String roleName,
                               boolean isActive,
                               Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.imageUrl = imageUrl;
        this.roleName = roleName;
        this.isActive = isActive;
        this.authorities = authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
}
