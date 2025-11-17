package com.mrbeans.circulosestudiobackend.security;

import com.mrbeans.circulosestudiobackend.user.entity.UserEntity;
import com.mrbeans.circulosestudiobackend.user.repositories.IUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final IUserRepository userRepository;

    public CustomUserDetailService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
        }

        String roleName = user.getRole().getName();
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase());
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(authority);

        return new CustomUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getImageDocument().getUrl(),
                user.getRole().getName(),
                user.isActive(),
                authorities
        );
    }
}
