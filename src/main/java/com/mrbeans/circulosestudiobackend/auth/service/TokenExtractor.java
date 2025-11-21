package com.mrbeans.circulosestudiobackend.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Service interface for extracting tokens from different sources.
 * Provides a unified interface to retrieve tokens from Authorization headers,
 * cookies, and SecurityContext with proper priority handling.
 */
public interface TokenExtractor {

    /**
     * Extracts token from Authorization header (Bearer tokens).
     *
     * @param request The HTTP request
     * @return Optional containing the token if found, empty otherwise
     */
    Optional<String> extractTokenFromHeader(HttpServletRequest request);

    /**
     * Extracts token from cookies (access_token).
     *
     * @param request The HTTP request
     * @return Optional containing the token if found, empty otherwise
     */
    Optional<String> extractTokenFromCookie(HttpServletRequest request);

    /**
     * Checks SecurityContext for authenticated users and extracts token information.
     *
     * @param request The HTTP request
     * @return Optional containing the authentication if found, empty otherwise
     */
    Optional<Authentication> getAuthenticationFromSecurityContext();

    /**
     * Provides a unified method to get tokens from any source with priority order:
     * 1. Authorization header (Bearer tokens)
     * 2. Cookies (access_token)
     * 3. SecurityContext (authenticated users)
     *
     * @param request The HTTP request
     * @return TokenExtractionResult containing the extraction result
     */
    TokenExtractionResult extractTokenWithPriority(HttpServletRequest request);

    /**
     * Masks sensitive token values for logging purposes.
     *
     * @param token The token to mask
     * @return The masked token
     */
    String maskToken(String token);

    /**
     * Result class to hold the token extraction result with source information.
     */
    class TokenExtractionResult {
        private final String token;
        private final TokenSource source;
        private final Authentication authentication;

        public TokenExtractionResult(String token, TokenSource source) {
            this.token = token;
            this.source = source;
            this.authentication = null;
        }

        public TokenExtractionResult(Authentication authentication) {
            this.token = null;
            this.source = TokenSource.SECURITY_CONTEXT;
            this.authentication = authentication;
        }

        public Optional<String> getToken() {
            return Optional.ofNullable(token);
        }

        public TokenSource getSource() {
            return source;
        }

        public Optional<Authentication> getAuthentication() {
            return Optional.ofNullable(authentication);
        }

        public boolean hasToken() {
            return token != null && !token.isEmpty();
        }

        public boolean hasAuthentication() {
            return authentication != null && authentication.isAuthenticated();
        }

        public boolean isAuthenticated() {
            return hasToken() || hasAuthentication();
        }
    }

    /**
     * Enumeration of token sources for tracking where the token was extracted from.
     */
    enum TokenSource {
        HEADER("Authorization Header"),
        COOKIE("Cookie"),
        SECURITY_CONTEXT("Security Context");

        private final String description;

        TokenSource(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}