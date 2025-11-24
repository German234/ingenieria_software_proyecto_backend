package com.mrbeans.circulosestudiobackend.security;

import com.mrbeans.circulosestudiobackend.user.entity.UserEntity;
import com.mrbeans.circulosestudiobackend.user.repositories.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Collection;
import java.util.UUID;

@Slf4j
@Component
public class CustomUserPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    private final IUserRepository userRepository;

    public CustomUserPrincipalArgumentResolver(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // Check if the parameter type is CustomUserPrincipal and has @AuthenticationPrincipal annotation
        return parameter.getParameterType().equals(CustomUserPrincipal.class) &&
               parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, 
                                 ModelAndViewContainer mavContainer,
                                 NativeWebRequest webRequest,
                                 WebDataBinderFactory binderFactory) throws Exception {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Handle case where user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("User is not authenticated");
            return null;
        }

        // Extract email from the authentication principal
        String email = extractEmailFromAuthentication(authentication);
        
        if (email == null) {
            log.error("Could not extract email from authentication principal");
            return null;
        }

        // Find user in database
        UserEntity user = userRepository.findByEmail(email);
        
        // Handle case where user is not found in database
        if (user == null) {
            log.error("User not found in database for email: {}", email);
            return null;
        }

        // Create and return CustomUserPrincipal
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = extractRoleName(user);
        String imageUrl = extractImageUrl(user);
        
        return new CustomUserPrincipal(
            user.getId(),
            user.getEmail(),
            user.getPassword(),
            user.getName(),
            imageUrl,
            roleName,
            user.isActive(),
            authorities
        );
    }

    private String extractEmailFromAuthentication(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
            Jwt jwt = (Jwt) jwtToken.getCredentials();
            
            // Try to get email from preferred_username claim first, then from email claim, then from subject
            String email = jwt.getClaim("preferred_username");
            if (email == null) {
                email = jwt.getClaim("email");
            }
            if (email == null) {
                email = jwt.getSubject();
            }
            
            log.debug("Extracted email from JWT: {}", email);
            return email;
        }
        
        // For other authentication types, try to get the name directly
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            return (String) principal;
        }
        
        log.error("Unsupported authentication type: {}", authentication.getClass().getSimpleName());
        return null;
    }

    private String extractRoleName(UserEntity user) {
        if (user.getRole() != null && user.getRole().getName() != null) {
            return user.getRole().getName();
        }
        return "USER"; // Default role
    }

    private String extractImageUrl(UserEntity user) {
        if (user.getImageDocument() != null) {
            // Get the URL from the document entity
            return user.getImageDocument().getUrl();
        }
        return null;
    }
}