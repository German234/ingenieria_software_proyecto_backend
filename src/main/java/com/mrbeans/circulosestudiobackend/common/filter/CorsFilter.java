package com.mrbeans.circulosestudiobackend.common.filter;

import com.mrbeans.circulosestudiobackend.common.config.CorsProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter extends OncePerRequestFilter {

    private final CorsProperties corsProperties;

    public CorsFilter(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String origin = request.getHeader("Origin");
        String requestMethod = request.getMethod();
        String requestPath = request.getRequestURI();
        
        log.debug("CORS Filter - Processing request: {} {} from origin: {}", requestMethod, requestPath, origin);

        // Always set CORS headers for OPTIONS requests (preflight)
        if ("OPTIONS".equalsIgnoreCase(requestMethod)) {
            log.info("CORS Filter - Handling OPTIONS preflight request for: {} {}", requestMethod, requestPath);
            
            // Set Access-Control-Allow-Origin
            if (origin != null && isAllowedOrigin(origin)) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                log.debug("CORS Filter - Set Access-Control-Allow-Origin to: {}", origin);
            } else {
                response.setHeader("Access-Control-Allow-Origin", "*");
                log.debug("CORS Filter - Set Access-Control-Allow-Origin to: *");
            }
            
            // Set Access-Control-Allow-Methods
            List<String> allowedMethods = corsProperties.getAllowedMethods();
            response.setHeader("Access-Control-Allow-Methods", String.join(", ", allowedMethods));
            log.debug("CORS Filter - Set Access-Control-Allow-Methods to: {}", allowedMethods);
            
            // Set Access-Control-Allow-Headers - include all common headers and requested headers
            String requestHeaders = request.getHeader("Access-Control-Request-Headers");
            List<String> allowedHeaders = corsProperties.getAllowedHeaders();
            
            StringBuilder headersToAllow = new StringBuilder();
            if (allowedHeaders.contains("*")) {
                // If wildcard is configured, allow all requested headers plus common ones
                if (requestHeaders != null && !requestHeaders.trim().isEmpty()) {
                    headersToAllow.append(requestHeaders);
                }
                // Always include common headers
                if (headersToAllow.length() > 0) {
                    headersToAllow.append(", ");
                }
                headersToAllow.append("Authorization, Content-Type, Accept, Origin, X-Requested-With, Cache-Control, X-Auth-Token");
            } else {
                // Use specific headers from configuration
                headersToAllow.append(String.join(", ", allowedHeaders));
                // Add requested headers if not already included
                if (requestHeaders != null && !requestHeaders.trim().isEmpty()) {
                    for (String header : requestHeaders.split(",")) {
                        header = header.trim();
                        if (!allowedHeaders.contains(header)) {
                            headersToAllow.append(", ").append(header);
                        }
                    }
                }
            }
            
            response.setHeader("Access-Control-Allow-Headers", headersToAllow.toString());
            log.debug("CORS Filter - Set Access-Control-Allow-Headers to: {}", headersToAllow.toString());
            
            // Set Access-Control-Allow-Credentials
            response.setHeader("Access-Control-Allow-Credentials", String.valueOf(corsProperties.isAllowCredentials()));
            log.debug("CORS Filter - Set Access-Control-Allow-Credentials to: {}", corsProperties.isAllowCredentials());
            
            // Set Access-Control-Max-Age
            response.setHeader("Access-Control-Max-Age", String.valueOf(corsProperties.getMaxAge()));
            
            // Set Vary header to handle multiple origins
            response.setHeader("Vary", "Origin");
            
            // Return 200 OK for preflight requests
            response.setStatus(HttpServletResponse.SC_OK);
            log.info("CORS Filter - Preflight request completed with status 200 OK");
            return;
        }

        // For non-OPTIONS requests, set CORS headers if origin is allowed
        if (origin != null && isAllowedOrigin(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", String.valueOf(corsProperties.isAllowCredentials()));
            response.setHeader("Vary", "Origin");
            log.debug("CORS Filter - Set CORS headers for allowed origin: {}", origin);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAllowedOrigin(String origin) {
        List<String> allowedOrigins = corsProperties.getAllowedOrigins();
        boolean isAllowed = allowedOrigins.contains("*") || allowedOrigins.contains(origin);
        log.debug("CORS Filter - Origin {} is allowed: {}", origin, isAllowed);
        return isAllowed;
    }
}