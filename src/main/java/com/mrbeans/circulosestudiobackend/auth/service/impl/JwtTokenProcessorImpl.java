package com.mrbeans.circulosestudiobackend.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrbeans.circulosestudiobackend.auth.service.JwtTokenProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of JwtTokenProcessor service.
 * Centralizes all JWT-related operations to eliminate code duplication.
 */
@Slf4j
@Service
public class JwtTokenProcessorImpl implements JwtTokenProcessor {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private final JwtDecoder jwtDecoder;
    private final ObjectMapper objectMapper;

    @Autowired
    public JwtTokenProcessorImpl(@Lazy JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String extractEmailFromToken(String token) {
        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            Map<String, Object> tokenData = objectMapper.readValue(payload, Map.class);
            return (String) tokenData.get("email");
        } catch (Exception e) {
            log.error("Error extracting email from JWT: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String extractUsernameFromToken(String token) {
        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            Map<String, Object> tokenData = objectMapper.readValue(payload, Map.class);
            return (String) tokenData.get("name");
        } catch (Exception e) {
            log.error("Error extracting username from JWT: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String extractSubjectFromToken(String token) {
        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            Map<String, Object> tokenData = objectMapper.readValue(payload, Map.class);
            return (String) tokenData.get("sub");
        } catch (Exception e) {
            log.error("Error extracting subject from JWT: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Jwt decodeToken(String token) throws JwtValidationException {
        try {
            return jwtDecoder.decode(token);
        } catch (JwtValidationException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error decoding JWT token: {}", e.getMessage());
            throw new JwtValidationException("Failed to decode token", Collections.emptyList());
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtValidationException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error during token validation: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Collection<GrantedAuthority> extractAuthoritiesFromToken(Jwt jwt) {
        // Extract realm roles from token
        Collection<String> realmRoles = null;
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");
            realmRoles = roles;
        }

        // Extract resource roles from token (similar to JwtConverterImpl)
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        Collection<String> resourceRoles = Set.of();
        if (resourceAccess != null) {
            for (Map.Entry<String, Object> entry : resourceAccess.entrySet()) {
                Object resourceValue = entry.getValue();
                if (resourceValue instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> resource = (Map<String, Object>) resourceValue;
                    if (resource.containsKey("roles")) {
                        @SuppressWarnings("unchecked")
                        List<String> roles = (List<String>) resource.get("roles");
                        if (resourceRoles.isEmpty()) {
                            resourceRoles = roles;
                        } else {
                            resourceRoles = Stream.concat(resourceRoles.stream(), roles.stream())
                                    .collect(Collectors.toList());
                        }
                    }
                }
            }
        }

        // Combine all roles and convert to authorities
        Stream<String> allRoles = Stream.concat(
                realmRoles != null ? realmRoles.stream() : Stream.empty(),
                resourceRoles != null ? resourceRoles.stream() : Stream.empty()
        );

        return allRoles
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<GrantedAuthority> extractAuthoritiesFromToken(String token) {
        try {
            Jwt jwt = decodeToken(token);
            return extractAuthoritiesFromToken(jwt);
        } catch (Exception e) {
            log.error("Error extracting authorities from token: {}", e.getMessage());
            return Collections.emptySet();
        }
    }

    @Override
    public UserClaims extractUserClaims(String token) {
        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            Map<String, Object> tokenData = objectMapper.readValue(payload, Map.class);
            String subject = (String) tokenData.get("sub");
            String username = (String) tokenData.get("name");
            String email = (String) tokenData.get("email");

            return new UserClaims(subject, username, email);
        } catch (Exception e) {
            log.error("Error extracting user claims from JWT: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<String> extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return Optional.of(authHeader.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> ACCESS_TOKEN_COOKIE.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst();
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> extractToken(HttpServletRequest request) {
        // Try to get token from Authorization header first
        Optional<String> tokenFromHeader = extractTokenFromHeader(request);
        if (tokenFromHeader.isPresent()) {
            log.debug("Using token from Authorization header");
            return tokenFromHeader;
        }

        // If no header, try to get token from cookies
        Optional<String> tokenFromCookie = extractTokenFromCookie(request);
        if (tokenFromCookie.isPresent()) {
            log.debug("Using token from access_token cookie");
            return tokenFromCookie;
        }

        return Optional.empty();
    }

    @Override
    public String maskToken(String token) {
        if (token == null) {
            return "null";
        }

        if (token.length() <= 8) {
            return "****";
        }

        return token.substring(0, 4) + "****" + token.substring(token.length() - 4);
    }
}