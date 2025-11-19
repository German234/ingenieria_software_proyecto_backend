package com.mrbeans.circulosestudiobackend.auth.service.impl;

import com.mrbeans.circulosestudiobackend.auth.service.JwtTokenProcessor;
import com.mrbeans.circulosestudiobackend.auth.service.TokenExtractor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenExtractorImplTest {

    @Mock
    private JwtTokenProcessor jwtTokenProcessor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TokenExtractorImpl tokenExtractor;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testExtractTokenFromHeader_WithValidBearerToken() {
        // Given
        String bearerToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenProcessor.maskToken(any())).thenReturn("****");

        // When
        Optional<String> result = tokenExtractor.extractTokenFromHeader(request);

        // Then
        assertTrue(result.isPresent());
        assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9", result.get());
        verify(jwtTokenProcessor, atLeastOnce()).maskToken(any());
    }

    @Test
    void testExtractTokenFromHeader_WithInvalidHeader() {
        // Given
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        // When
        Optional<String> result = tokenExtractor.extractTokenFromHeader(request);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testExtractTokenFromHeader_WithNoHeader() {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        Optional<String> result = tokenExtractor.extractTokenFromHeader(request);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testExtractTokenFromCookie_WithValidCookie() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        Cookie[] cookies = new Cookie[]{new Cookie("access_token", token)};
        when(request.getCookies()).thenReturn(cookies);
        when(jwtTokenProcessor.maskToken(any())).thenReturn("****");

        // When
        Optional<String> result = tokenExtractor.extractTokenFromCookie(request);

        // Then
        assertTrue(result.isPresent());
        assertEquals(token, result.get());
        verify(jwtTokenProcessor, atLeastOnce()).maskToken(any());
    }

    @Test
    void testExtractTokenFromCookie_WithNoCookies() {
        // Given
        when(request.getCookies()).thenReturn(null);

        // When
        Optional<String> result = tokenExtractor.extractTokenFromCookie(request);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testExtractTokenFromCookie_WithNoAccessTokenCookie() {
        // Given
        Cookie[] cookies = new Cookie[]{new Cookie("other_cookie", "value")};
        when(request.getCookies()).thenReturn(cookies);

        // When
        Optional<String> result = tokenExtractor.extractTokenFromCookie(request);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testGetAuthenticationFromSecurityContext_WithValidAuthentication() {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.setContext(securityContext);

        // When
        Optional<Authentication> result = tokenExtractor.getAuthenticationFromSecurityContext();

        // Then
        assertTrue(result.isPresent());
        assertEquals(authentication, result.get());
    }

    @Test
    void testGetAuthenticationFromSecurityContext_WithNoAuthentication() {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // When
        Optional<Authentication> result = tokenExtractor.getAuthenticationFromSecurityContext();

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testGetAuthenticationFromSecurityContext_WithUnauthenticatedUser() {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        // When
        Optional<Authentication> result = tokenExtractor.getAuthenticationFromSecurityContext();

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testExtractTokenWithPriority_HeaderTakesPrecedence() {
        // Given
        String headerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.header";
        String cookieToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.cookie";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + headerToken);
        Cookie[] cookies = new Cookie[]{new Cookie("access_token", cookieToken)};
        lenient().when(request.getCookies()).thenReturn(cookies);

        // When
        TokenExtractor.TokenExtractionResult result = tokenExtractor.extractTokenWithPriority(request);

        // Then
        assertTrue(result.isAuthenticated());
        assertTrue(result.hasToken());
        assertEquals(headerToken, result.getToken().get());
        assertEquals(TokenExtractor.TokenSource.HEADER, result.getSource());
    }

    @Test
    void testExtractTokenWithPriority_CookieUsedWhenNoHeader() {
        // Given
        String cookieToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.cookie";
        
        when(request.getHeader("Authorization")).thenReturn(null);
        Cookie[] cookies = new Cookie[]{new Cookie("access_token", cookieToken)};
        when(request.getCookies()).thenReturn(cookies);

        // When
        TokenExtractor.TokenExtractionResult result = tokenExtractor.extractTokenWithPriority(request);

        // Then
        assertTrue(result.isAuthenticated());
        assertTrue(result.hasToken());
        assertEquals(cookieToken, result.getToken().get());
        assertEquals(TokenExtractor.TokenSource.COOKIE, result.getSource());
    }

    @Test
    void testExtractTokenWithPriority_SecurityContextUsedWhenNoToken() {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.setContext(securityContext);

        // When
        TokenExtractor.TokenExtractionResult result = tokenExtractor.extractTokenWithPriority(request);

        // Then
        assertTrue(result.isAuthenticated());
        assertFalse(result.hasToken());
        assertTrue(result.hasAuthentication());
        assertEquals(authentication, result.getAuthentication().get());
        assertEquals(TokenExtractor.TokenSource.SECURITY_CONTEXT, result.getSource());
    }

    @Test
    void testExtractTokenWithPriority_NoAuthenticationFound() {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // When
        TokenExtractor.TokenExtractionResult result = tokenExtractor.extractTokenWithPriority(request);

        // Then
        assertFalse(result.isAuthenticated());
        assertFalse(result.hasToken());
        assertFalse(result.hasAuthentication());
    }

    @Test
    void testMaskToken() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        when(jwtTokenProcessor.maskToken(token)).thenReturn("****");
        
        // When
        String result = tokenExtractor.maskToken(token);

        // Then
        assertEquals("****", result);
        verify(jwtTokenProcessor).maskToken(token);
    }
}