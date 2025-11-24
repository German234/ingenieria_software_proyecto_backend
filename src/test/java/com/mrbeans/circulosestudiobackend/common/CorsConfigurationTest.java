package com.mrbeans.circulosestudiobackend.common;

import com.mrbeans.circulosestudiobackend.common.config.CorsProperties;
import com.mrbeans.circulosestudiobackend.common.filter.CorsFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CorsConfigurationTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private CorsProperties corsProperties;

    @InjectMocks
    private CorsFilter corsFilter;

    @BeforeEach
    void setUp() {
        // Setup default CORS properties
        when(corsProperties.getAllowedOrigins()).thenReturn(Arrays.asList("http://localhost:3000", "http://localhost:3001"));
        when(corsProperties.getAllowedMethods()).thenReturn(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        when(corsProperties.getAllowedHeaders()).thenReturn(Arrays.asList("Authorization", "Content-Type", "*"));
        when(corsProperties.isAllowCredentials()).thenReturn(true);
        when(corsProperties.getMaxAge()).thenReturn(3600L);
    }

    @Test
    void testOptionsPreflightRequest() throws Exception {
        // Setup OPTIONS request
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getHeader("Origin")).thenReturn("http://localhost:3000");
        when(request.getRequestURI()).thenReturn("/api/test");

        // Execute filter
        corsFilter.doFilterInternal(request, response, filterChain);

        // Verify CORS headers are set
        verify(response).setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        verify(response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        verify(response).setHeader(eq("Access-Control-Allow-Headers"), any(String.class));
        verify(response).setHeader("Access-Control-Allow-Credentials", "true");
        verify(response).setHeader("Access-Control-Max-Age", "3600");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        // Verify filter chain is NOT continued for OPTIONS (should return early)
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testNonOptionsRequest() throws Exception {
        // Setup regular GET request
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Origin")).thenReturn("http://localhost:3000");
        when(request.getRequestURI()).thenReturn("/api/test");

        // Execute filter
        corsFilter.doFilterInternal(request, response, filterChain);

        // Verify CORS headers are set for allowed origin
        verify(response).setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        verify(response).setHeader("Access-Control-Allow-Credentials", "true");

        // Verify filter chain continues for non-OPTIONS requests
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testRequestWithDisallowedOrigin() throws Exception {
        // Setup request with disallowed origin
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Origin")).thenReturn("http://malicious-site.com");
        when(request.getRequestURI()).thenReturn("/api/test");

        // Execute filter
        corsFilter.doFilterInternal(request, response, filterChain);

        // Verify no CORS headers are set for disallowed origin
        verify(response, never()).setHeader(eq("Access-Control-Allow-Origin"), any(String.class));

        // Verify filter chain still continues
        verify(filterChain).doFilter(request, response);
    }
}