package com.mrbeans.circulosestudiobackend.keycloak.service;

import com.mrbeans.circulosestudiobackend.keycloak.dtos.KeycloakTokenResponse;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.UMAResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Service interface for handling Keycloak authentication and authorization operations.
 * This service encapsulates all Keycloak-specific communication to improve testability
 * and maintainability.
 */
public interface KeycloakService {

    /**
     * Generates the authorization URL for Keycloak login.
     *
     * @return The authorization URL
     */
    String generateAuthorizationUrl();

    /**
     * Exchanges authorization code for access and refresh tokens.
     *
     * @param code The authorization code received from Keycloak
     * @param response HttpServletResponse to set cookies
     * @return ResponseEntity with the token response or error
     */
    ResponseEntity<KeycloakTokenResponse> exchangeCodeForTokens(String code, HttpServletResponse response);

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param refreshToken The refresh token
     * @return ResponseEntity with the new token response or error
     */
    ResponseEntity<KeycloakTokenResponse> refreshToken(String refreshToken);

    /**
     * Logs out from Keycloak by invalidating the refresh token.
     *
     * @param refreshToken The refresh token to invalidate
     * @return ResponseEntity indicating success or failure
     */
    ResponseEntity<String> logout(String refreshToken);

    /**
     * Fetches UMA (User-Managed Access) permissions from Keycloak for a given access token.
     *
     * @param accessToken The access token
     * @return List of UMA permissions
     */
    List<UMAResponse> fetchUMAPermissions(String accessToken);

    /**
     * Extracts user information from a JWT token.
     *
     * @param accessToken The access token
     * @return UserInfoDTO containing user details
     */
    com.mrbeans.circulosestudiobackend.keycloak.dtos.UserInfoDTO extractUserInfo(String accessToken);

    /**
     * Extracts email from a JWT token.
     *
     * @param token The JWT token
     * @return The email address extracted from the token
     */
    String extractEmailFromToken(String token);
}