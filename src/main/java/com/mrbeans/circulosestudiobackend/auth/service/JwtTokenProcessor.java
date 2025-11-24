package com.mrbeans.circulosestudiobackend.auth.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidationException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for processing JWT tokens.
 * Centralizes all JWT-related operations to eliminate code duplication
 * between AuthController and CookieTokenFilter.
 */
public interface JwtTokenProcessor {

    /**
     * Extracts the email claim from a JWT token.
     *
     * @param token The JWT token
     * @return The email claim, or null if not found
     */
    String extractEmailFromToken(String token);

    /**
     * Extracts the username claim from a JWT token.
     *
     * @param token The JWT token
     * @return The username claim, or null if not found
     */
    String extractUsernameFromToken(String token);

    /**
     * Extracts the subject (sub) claim from a JWT token.
     *
     * @param token The JWT token
     * @return The subject claim, or null if not found
     */
    String extractSubjectFromToken(String token);

    /**
     * Decodes a JWT token and returns the Jwt object.
     *
     * @param token The JWT token to decode
     * @return The decoded Jwt object
     * @throws JwtValidationException if the token is invalid
     */
    Jwt decodeToken(String token) throws JwtValidationException;

    /**
     * Validates a JWT token.
     *
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Extracts authorities/roles from a JWT token.
     *
     * @param jwt The decoded JWT token
     * @return Collection of authorities
     */
    Collection<GrantedAuthority> extractAuthoritiesFromToken(Jwt jwt);

    /**
     * Extracts authorities/roles from a JWT token string.
     *
     * @param token The JWT token string
     * @return Collection of authorities
     */
    Collection<GrantedAuthority> extractAuthoritiesFromToken(String token);

    /**
     * Extracts all user claims (email, username, sub) from a JWT token.
     *
     * @param token The JWT token
     * @return UserClaims object containing the extracted claims
     */
    UserClaims extractUserClaims(String token);

    /**
     * Extracts JWT token from Authorization header.
     *
     * @param request The HTTP request
     * @return Optional containing the token if found, empty otherwise
     */
    Optional<String> extractTokenFromHeader(HttpServletRequest request);

    /**
     * Extracts JWT token from cookies.
     *
     * @param request The HTTP request
     * @return Optional containing the token if found, empty otherwise
     */
    Optional<String> extractTokenFromCookie(HttpServletRequest request);

    /**
     * Extracts JWT token from either Authorization header or cookies.
     * Header takes precedence over cookies.
     *
     * @param request The HTTP request
     * @return Optional containing the token if found, empty otherwise
     */
    Optional<String> extractToken(HttpServletRequest request);

    /**
     * Masks sensitive token values for logging purposes.
     *
     * @param token The token to mask
     * @return The masked token
     */
    String maskToken(String token);

    /**
     * Data class to hold user claims extracted from JWT.
     */
    class UserClaims {
        private final String subject;
        private final String username;
        private final String email;

        public UserClaims(String subject, String username, String email) {
            this.subject = subject;
            this.username = username;
            this.email = email;
        }

        public String getSubject() {
            return subject;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }
}