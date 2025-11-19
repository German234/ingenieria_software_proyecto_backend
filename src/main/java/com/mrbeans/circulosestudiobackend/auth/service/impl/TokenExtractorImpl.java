package com.mrbeans.circulosestudiobackend.auth.service.impl;

import com.mrbeans.circulosestudiobackend.auth.service.JwtTokenProcessor;
import com.mrbeans.circulosestudiobackend.auth.service.TokenExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

/**
 * Implementation of TokenExtractor service.
 * Provides unified token extraction from different sources with proper priority handling.
 */
@Slf4j
@Service
public class TokenExtractorImpl implements TokenExtractor {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProcessor jwtTokenProcessor;

    @Autowired
    public TokenExtractorImpl(JwtTokenProcessor jwtTokenProcessor) {
        this.jwtTokenProcessor = jwtTokenProcessor;
    }

    @Override
    public Optional<String> extractTokenFromHeader(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);
            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                String token = authHeader.substring(BEARER_PREFIX.length());
                log.debug("Extracted token from Authorization header: {}", maskToken(token));
                return Optional.of(token);
            }
        } catch (Exception e) {
            log.error("Error extracting token from Authorization header: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> extractTokenFromCookie(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Optional<String> token = Arrays.stream(cookies)
                        .filter(cookie -> ACCESS_TOKEN_COOKIE.equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst();

                if (token.isPresent()) {
                    log.debug("Extracted token from {} cookie: {}", ACCESS_TOKEN_COOKIE, maskToken(token.get()));
                    return token;
                }
            }
        } catch (Exception e) {
            log.error("Error extracting token from cookies: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Authentication> getAuthenticationFromSecurityContext() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                log.debug("Found authenticated user in SecurityContext: {}", authentication.getName());
                return Optional.of(authentication);
            }
        } catch (Exception e) {
            log.error("Error extracting authentication from SecurityContext: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public TokenExtractionResult extractTokenWithPriority(HttpServletRequest request) {
        // Priority 1: Check Authorization header first
        Optional<String> tokenFromHeader = extractTokenFromHeader(request);
        if (tokenFromHeader.isPresent()) {
            log.debug("Using token from Authorization header (priority 1)");
            return new TokenExtractionResult(tokenFromHeader.get(), TokenSource.HEADER);
        }

        // Priority 2: Check cookies if no header token found
        Optional<String> tokenFromCookie = extractTokenFromCookie(request);
        if (tokenFromCookie.isPresent()) {
            log.debug("Using token from {} cookie (priority 2)", ACCESS_TOKEN_COOKIE);
            return new TokenExtractionResult(tokenFromCookie.get(), TokenSource.COOKIE);
        }

        // Priority 3: Check SecurityContext if no token found in header or cookies
        Optional<Authentication> authentication = getAuthenticationFromSecurityContext();
        if (authentication.isPresent()) {
            log.debug("Using authentication from SecurityContext (priority 3)");
            return new TokenExtractionResult(authentication.get());
        }

        log.debug("No token or authentication found in any source");
        // Return an empty result - no token found in any source
        return new TokenExtractionResult((String) null, TokenSource.HEADER) {
            @Override
            public boolean isAuthenticated() {
                return false;
            }
        };
    }

    @Override
    public String maskToken(String token) {
        // Reuse the JwtTokenProcessor's masking functionality
        return jwtTokenProcessor.maskToken(token);
    }
}