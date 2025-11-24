package com.mrbeans.circulosestudiobackend.auth.service.impl;

import com.mrbeans.circulosestudiobackend.auth.service.JwtTokenProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProcessorImplTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private Jwt jwt;

    private JwtTokenProcessor jwtTokenProcessor;

    @BeforeEach
    void setUp() {
        jwtTokenProcessor = new JwtTokenProcessorImpl(jwtDecoder, "test-resource-id");
    }

    @Test
    void testExtractEmailFromToken_ValidToken_ReturnsEmail() {
        // Create a mock JWT token payload with email claim
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiZW1haWwiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        String email = jwtTokenProcessor.extractEmailFromToken(token);
        
        assertEquals("john.doe@example.com", email);
    }

    @Test
    void testExtractEmailFromToken_InvalidToken_ReturnsNull() {
        String invalidToken = "invalid.token.here";
        
        String email = jwtTokenProcessor.extractEmailFromToken(invalidToken);
        
        assertNull(email);
    }

    @Test
    void testExtractUsernameFromToken_ValidToken_ReturnsUsername() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiZW1haWwiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        String username = jwtTokenProcessor.extractUsernameFromToken(token);
        
        assertEquals("John Doe", username);
    }

    @Test
    void testExtractSubjectFromToken_ValidToken_ReturnsSubject() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiZW1haWwiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        String subject = jwtTokenProcessor.extractSubjectFromToken(token);
        
        assertEquals("1234567890", subject);
    }

    @Test
    void testDecodeToken_ValidToken_ReturnsJwt() {
        String token = "valid.jwt.token";
        when(jwtDecoder.decode(token)).thenReturn(jwt);
        
        Jwt result = jwtTokenProcessor.decodeToken(token);
        
        assertEquals(jwt, result);
        verify(jwtDecoder).decode(token);
    }

    @Test
    void testDecodeToken_InvalidToken_ThrowsJwtValidationException() {
        String invalidToken = "invalid.token";
        when(jwtDecoder.decode(invalidToken)).thenThrow(JwtValidationException.class);
        
        assertThrows(JwtValidationException.class, () -> jwtTokenProcessor.decodeToken(invalidToken));
    }

    @Test
    void testValidateToken_ValidToken_ReturnsTrue() {
        String validToken = "valid.jwt.token";
        when(jwtDecoder.decode(validToken)).thenReturn(jwt);
        
        boolean result = jwtTokenProcessor.validateToken(validToken);
        
        assertTrue(result);
    }

    @Test
    void testValidateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token";
        when(jwtDecoder.decode(invalidToken)).thenThrow(JwtValidationException.class);
        
        boolean result = jwtTokenProcessor.validateToken(invalidToken);
        
        assertFalse(result);
    }

    @Test
    void testExtractUserClaims_ValidToken_ReturnsUserClaims() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiZW1haWwiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        JwtTokenProcessor.UserClaims claims = jwtTokenProcessor.extractUserClaims(token);
        
        assertNotNull(claims);
        assertEquals("1234567890", claims.getSubject());
        assertEquals("John Doe", claims.getUsername());
        assertEquals("john.doe@example.com", claims.getEmail());
    }

    @Test
    void testExtractUserClaims_InvalidToken_ReturnsNull() {
        String invalidToken = "invalid.token.here";
        
        JwtTokenProcessor.UserClaims claims = jwtTokenProcessor.extractUserClaims(invalidToken);
        
        assertNull(claims);
    }

    @Test
    void testMaskToken_ValidToken_ReturnsMaskedToken() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiZW1haWwiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        String maskedToken = jwtTokenProcessor.maskToken(token);
        
        assertTrue(maskedToken.startsWith("eyJ"));
        assertTrue(maskedToken.endsWith("w5c"));
        assertTrue(maskedToken.contains("****"));
    }

    @Test
    void testMaskToken_ShortToken_ReturnsFullyMasked() {
        String shortToken = "short";
        
        String maskedToken = jwtTokenProcessor.maskToken(shortToken);
        
        assertEquals("****", maskedToken);
    }

    @Test
    void testMaskToken_NullToken_ReturnsNullString() {
        String maskedToken = jwtTokenProcessor.maskToken(null);
        
        assertEquals("null", maskedToken);
    }
}
