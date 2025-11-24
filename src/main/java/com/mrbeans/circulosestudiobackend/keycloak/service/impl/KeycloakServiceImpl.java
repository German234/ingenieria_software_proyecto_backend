package com.mrbeans.circulosestudiobackend.keycloak.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrbeans.circulosestudiobackend.keycloak.client.iKeycloakAuthClient;
import com.mrbeans.circulosestudiobackend.keycloak.config.KeycloakProperties;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.KeycloakTokenResponse;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.UMAResponse;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.UserInfoDTO;
import com.mrbeans.circulosestudiobackend.keycloak.service.KeycloakService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of KeycloakService that handles communication with Keycloak.
 * This service encapsulates all Keycloak-specific operations including token exchange,
 * refresh, logout, and permission fetching.
 */
@Slf4j
@Service
public class KeycloakServiceImpl implements KeycloakService {

    private final iKeycloakAuthClient keycloakAuthClient;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KeycloakProperties keycloakProperties;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;

    @Value("${keycloak.redirect.uri}")
    private String redirectUri;

    @Value("${keycloak.auth.url}")
    private String authUrl;

    @Value("${keycloak.base.url}")
    private String baseUrl;

    @Value("${keycloak.token.url}")
    private String tokenUrl;

    @Value("${keycloak.realm}")
    private String realm;

    private final String scope = "openid profile email";

    public KeycloakServiceImpl(iKeycloakAuthClient keycloakAuthClient,
                              RestTemplate restTemplate,
                              ObjectMapper objectMapper,
                              KeycloakProperties keycloakProperties) {
        this.keycloakAuthClient = keycloakAuthClient;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.keycloakProperties = keycloakProperties;
    }

    @Override
    public String generateAuthorizationUrl() {
        log.debug("Generating authorization URL for Keycloak");
        return UriComponentsBuilder.fromUriString(authUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", scope)
                .build().toUriString();
    }

    @Override
    public ResponseEntity<KeycloakTokenResponse> exchangeCodeForTokens(String code, HttpServletResponse response) {
        log.debug("Exchanging authorization code for tokens");
        
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("code", code);
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("redirect_uri", redirectUri);

            KeycloakTokenResponse tokenResponse = keycloakAuthClient.getToken(formData);
            
            if (tokenResponse != null && tokenResponse.getAccessToken() != null) {
                // Set cookies for the tokens
                response.addHeader("Set-Cookie", "access_token=" + tokenResponse.getAccessToken() +
                        "; Path=/; Max-Age=3600; HttpOnly; SameSite=Lax");
                
                if (tokenResponse.getRefreshToken() != null) {
                    response.addHeader("Set-Cookie", "refresh_token=" + tokenResponse.getRefreshToken() +
                            "; Path=/; Max-Age=604800; HttpOnly; SameSite=Lax");
                }
            }
            
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            log.error("Error exchanging code for tokens: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<KeycloakTokenResponse> refreshToken(String refreshToken) {
        log.debug("Refreshing access token");
        
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", refreshToken);
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);

            KeycloakTokenResponse tokenResponse = keycloakAuthClient.getToken(formData);
            
            if (tokenResponse != null && tokenResponse.getAccessToken() != null) {
                return ResponseEntity.ok(tokenResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<String> logout(String refreshToken) {
        log.debug("Logging out from Keycloak");
        
        try {
            String logoutUrl = baseUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("client_id", clientId);
            form.add("client_secret", clientSecret);
            form.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(logoutUrl, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok("Successfully logged out from Keycloak.");
            } else {
                log.warn("Logout from Keycloak returned non-success status: {}", response.getStatusCode());
                return ResponseEntity.ok("Logout request failed.");
            }
        } catch (Exception e) {
            log.error("Error logging out from Keycloak: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to logout from Keycloak");
        }
    }

    @Override
    public List<UMAResponse> fetchUMAPermissions(String accessToken) {
        log.debug("Fetching UMA permissions from Keycloak");
        
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "urn:ietf:params:oauth:grant-type:uma-ticket");
            formData.add("audience", clientId);
            formData.add("response_mode", "permissions");

            List<UMAResponse> permissions = keycloakAuthClient.requestPermissionTicket("Bearer " + accessToken, formData);
            
            if (permissions != null) {
                log.debug("Successfully fetched {} UMA permissions", permissions.size());
                return permissions;
            } else {
                log.warn("Received null permissions response from Keycloak");
                return List.of();
            }
        } catch (Exception e) {
            log.error("Error fetching UMA permissions from Keycloak: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public UserInfoDTO extractUserInfo(String accessToken) {
        log.debug("Extracting user information from token");
        
        try {
            String[] chunks = accessToken.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            Map<String, Object> tokenData = objectMapper.readValue(payload, Map.class);
            String id = (String) tokenData.get("sub");
            String username = (String) tokenData.get("name");
            String email = (String) tokenData.get("email");

            // Fetch UMA permissions
            List<UMAResponse> umaResponses = fetchUMAPermissions(accessToken);
            List<String> permisos = umaResponses.stream()
                    .filter(permission -> permission.getScopes() != null)
                    .flatMap(p -> p.getScopes().stream())
                    .distinct()
                    .collect(Collectors.toList());

            return new UserInfoDTO(id, username, email, permisos);
        } catch (Exception e) {
            log.error("Error extracting user info from token: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String extractEmailFromToken(String token) {
        log.debug("Extracting email from token");
        
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
}