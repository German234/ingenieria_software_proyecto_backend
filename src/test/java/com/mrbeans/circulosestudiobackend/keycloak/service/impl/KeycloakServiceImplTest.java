package com.mrbeans.circulosestudiobackend.keycloak.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrbeans.circulosestudiobackend.keycloak.client.iKeycloakAuthClient;
import com.mrbeans.circulosestudiobackend.keycloak.config.KeycloakProperties;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.KeycloakTokenResponse;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.UserInfoDTO;
import com.mrbeans.circulosestudiobackend.keycloak.service.KeycloakService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakServiceImplTest {

    @Mock
    private iKeycloakAuthClient keycloakAuthClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KeycloakProperties keycloakProperties;

    @Mock
    private HttpServletResponse response;

    private KeycloakService keycloakService;

    @BeforeEach
    void setUp() {
        keycloakService = new KeycloakServiceImpl(keycloakAuthClient, restTemplate, objectMapper, keycloakProperties);
        
        // Set up configuration values using reflection
        ReflectionTestUtils.setField(keycloakService, "clientId", "test-client");
        ReflectionTestUtils.setField(keycloakService, "clientSecret", "test-secret");
        ReflectionTestUtils.setField(keycloakService, "redirectUri", "http://localhost:3000/callback");
        ReflectionTestUtils.setField(keycloakService, "authUrl", "http://keycloak/auth");
        ReflectionTestUtils.setField(keycloakService, "baseUrl", "http://keycloak");
        ReflectionTestUtils.setField(keycloakService, "tokenUrl", "http://keycloak/token");
        ReflectionTestUtils.setField(keycloakService, "realm", "test-realm");
    }

    @Test
    void testGenerateAuthorizationUrl() {
        // When
        String authorizationUrl = keycloakService.generateAuthorizationUrl();

        // Then
        assertNotNull(authorizationUrl);
        assertTrue(authorizationUrl.contains("response_type=code"));
        assertTrue(authorizationUrl.contains("client_id=test-client"));
        assertTrue(authorizationUrl.contains("redirect_uri=http://localhost:3000/callback"));
        assertTrue(authorizationUrl.contains("scope=openid profile email"));
    }

    @Test
    void testExtractEmailFromToken_ValidToken() throws Exception {
        // Given
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"1234567890\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        String signature = "signature";
        
        String token = Base64.getUrlEncoder().encodeToString(header.getBytes()) + "." +
                     Base64.getUrlEncoder().encodeToString(payload.getBytes()) + "." +
                     Base64.getUrlEncoder().encodeToString(signature.getBytes());

        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("email", "john.doe@example.com");
        
        when(objectMapper.readValue(anyString(), eq(Map.class))).thenReturn(tokenData);

        // When
        String email = keycloakService.extractEmailFromToken(token);

        // Then
        assertEquals("john.doe@example.com", email);
    }

    @Test
    void testExtractEmailFromToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";
        
        try {
            doThrow(com.fasterxml.jackson.core.JsonProcessingException.class).when(objectMapper).readValue(anyString(), eq(Map.class));
        } catch (Exception e) {
            // This is just for compilation purposes
        }

        // When
        String email = keycloakService.extractEmailFromToken(invalidToken);

        // Then
        assertNull(email);
    }

    @Test
    void testExtractUserInfo_ValidToken() throws Exception {
        // Given
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"1234567890\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        String signature = "signature";
        
        String token = Base64.getUrlEncoder().encodeToString(header.getBytes()) + "." +
                     Base64.getUrlEncoder().encodeToString(payload.getBytes()) + "." +
                     Base64.getUrlEncoder().encodeToString(signature.getBytes());

        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("sub", "1234567890");
        tokenData.put("name", "John Doe");
        tokenData.put("email", "john.doe@example.com");
        
        when(objectMapper.readValue(anyString(), eq(Map.class))).thenReturn(tokenData);
        when(keycloakAuthClient.requestPermissionTicket(anyString(), any())).thenReturn(List.of());

        // When
        UserInfoDTO userInfo = keycloakService.extractUserInfo(token);

        // Then
        assertNotNull(userInfo);
        assertEquals("1234567890", userInfo.getId());
        assertEquals("John Doe", userInfo.getUsername());
        assertEquals("john.doe@example.com", userInfo.getEmail());
        assertNotNull(userInfo.getPermisos());
    }

    @Test
    void testExchangeCodeForTokens_Success() {
        // Given
        String code = "test-auth-code";
        KeycloakTokenResponse tokenResponse = new KeycloakTokenResponse();
        tokenResponse.setAccessToken("test-access-token");
        tokenResponse.setRefreshToken("test-refresh-token");
        tokenResponse.setExpiresIn("3600");
        tokenResponse.setTokenType("Bearer");
        
        when(keycloakAuthClient.getToken(any())).thenReturn(tokenResponse);

        // When
        ResponseEntity<KeycloakTokenResponse> response = keycloakService.exchangeCodeForTokens(code, this.response);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("test-access-token", response.getBody().getAccessToken());
        assertEquals("test-refresh-token", response.getBody().getRefreshToken());
        
        verify(this.response).addHeader(eq("Set-Cookie"), contains("access_token=test-access-token"));
        verify(this.response).addHeader(eq("Set-Cookie"), contains("refresh_token=test-refresh-token"));
    }

    @Test
    void testRefreshToken_Success() {
        // Given
        String refreshToken = "test-refresh-token";
        KeycloakTokenResponse tokenResponse = new KeycloakTokenResponse();
        tokenResponse.setAccessToken("new-access-token");
        tokenResponse.setExpiresIn("3600");
        tokenResponse.setTokenType("Bearer");
        
        when(keycloakAuthClient.getToken(any())).thenReturn(tokenResponse);

        // When
        ResponseEntity<KeycloakTokenResponse> response = keycloakService.refreshToken(refreshToken);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("new-access-token", response.getBody().getAccessToken());
    }

    @Test
    void testLogout_Success() {
        // Given
        String refreshToken = "test-refresh-token";
        ResponseEntity<String> mockResponse = ResponseEntity.ok("Logout successful");
        
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(mockResponse);

        // When
        ResponseEntity<String> response = keycloakService.logout(refreshToken);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Successfully logged out from Keycloak.", response.getBody());
    }
}