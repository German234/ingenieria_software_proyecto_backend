package com.mrbeans.circulosestudiobackend.common.filter;

import com.mrbeans.circulosestudiobackend.auth.service.JwtTokenProcessor;
import com.mrbeans.circulosestudiobackend.auth.service.TokenExtractor;
import com.mrbeans.circulosestudiobackend.security.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class CookieTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CookieTokenFilter.class);
    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final CustomUserDetailService customUserDetailService;
    private final JwtTokenProcessor jwtTokenProcessor;
    private final TokenExtractor tokenExtractor;
    
    @Autowired
    public CookieTokenFilter(CustomUserDetailService customUserDetailService,
                           JwtTokenProcessor jwtTokenProcessor,
                           TokenExtractor tokenExtractor) {
        this.customUserDetailService = customUserDetailService;
        this.jwtTokenProcessor = jwtTokenProcessor;
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String requestMethod = req.getMethod();
        String requestPath = req.getRequestURI();

        // Log request context
        logger.info("Processing request: {} {}", requestMethod, requestPath);

        // Use TokenExtractor to get token extraction result with priority
        TokenExtractor.TokenExtractionResult extractionResult = tokenExtractor.extractTokenWithPriority(req);
        
        // Log token source
        if (extractionResult.hasToken()) {
            logger.info("Token found from {}", extractionResult.getSource().getDescription());
            logger.debug("Token value: {}", jwtTokenProcessor.maskToken(extractionResult.getToken().get()));
        }

        // Get all cookies for logging purposes
        Cookie[] cookies = req.getCookies();
        if (cookies != null && cookies.length > 0) {
            // Log all cookies
            String allCookies = Arrays.stream(cookies)
                    .map(cookie -> String.format("%s=%s", cookie.getName(), maskSensitiveValue(cookie.getName(), cookie.getValue())))
                    .collect(java.util.stream.Collectors.joining(", "));

            logger.debug("All cookies in request: {}", allCookies);

            // Check for authentication-related cookies specifically
            boolean hasAccessToken = Arrays.stream(cookies)
                    .anyMatch(cookie -> ACCESS_TOKEN_COOKIE.equals(cookie.getName()));

            boolean hasRefreshToken = Arrays.stream(cookies)
                    .anyMatch(cookie -> REFRESH_TOKEN_COOKIE.equals(cookie.getName()));

            if (hasAccessToken || hasRefreshToken) {
                logger.info("Authentication cookies detected - Access Token: {}, Refresh Token: {}",
                        hasAccessToken, hasRefreshToken);

                // Log specific auth cookie values (masked)
                Arrays.stream(cookies)
                        .filter(cookie -> ACCESS_TOKEN_COOKIE.equals(cookie.getName()) || REFRESH_TOKEN_COOKIE.equals(cookie.getName()))
                        .forEach(cookie -> logger.debug("Auth cookie - {}: {}",
                                cookie.getName(), maskSensitiveValue(cookie.getName(), cookie.getValue())));
            }
        } else {
            logger.debug("No cookies present in request");
        }

        // Process authentication if token is found and no existing authentication
        if (extractionResult.hasToken() && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Processing authentication with token from {}", extractionResult.getSource().getDescription());
            processTokenAuthentication(req, extractionResult.getToken().get());
        } else if (extractionResult.hasAuthentication()) {
            logger.debug("User already authenticated in SecurityContext");
        } else {
            logger.debug("No token found for authentication");
        }

        chain.doFilter(req, res);
    }

    /**
     * Processes authentication using the provided token
     *
     * @param request The HTTP request
     * @param token The JWT token to process
     */
    private void processTokenAuthentication(HttpServletRequest request, String token) {
        try {
            logger.debug("Processing token authentication with token: {}", jwtTokenProcessor.maskToken(token));

            // Validate the token using JwtTokenProcessor
            if (!jwtTokenProcessor.validateToken(token)) {
                logger.warn("Invalid token provided for authentication");
                return;
            }

            logger.debug("Token validation successful, extracting user information");

            // Get user claims from token
            JwtTokenProcessor.UserClaims userClaims = jwtTokenProcessor.extractUserClaims(token);
            if (userClaims == null) {
                logger.warn("Failed to extract user claims from token");
                return;
            }

            // Get user email from claims
            String email = userClaims.getEmail();
            if (email == null) {
                email = userClaims.getSubject(); // Fallback to subject if email claim is not present
            }

            if (email == null) {
                logger.warn("No email or subject found in token claims");
                return;
            }

            // Extract authorities from token using JwtTokenProcessor
            var authorities = jwtTokenProcessor.extractAuthoritiesFromToken(token);

            // Create authentication object with token and authorities
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email, null, authorities
            );

            // Set authentication details
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            logger.info("Successfully authenticated user: {}", email);
        } catch (JwtValidationException e) {
            logger.warn("Invalid token: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing token authentication: {}", e.getMessage(), e);
        }
    }

    /**
     * Masks sensitive cookie values for logging purposes
     *
     * @param cookieName  The name of the cookie
     * @param cookieValue The value of the cookie
     * @return The original value for non-sensitive cookies, or a masked version for sensitive ones
     */
    private String maskSensitiveValue(String cookieName, String cookieValue) {
        if (cookieValue == null) {
            return "null";
        }

        // Mask authentication-related cookies
        if (ACCESS_TOKEN_COOKIE.equals(cookieName) || REFRESH_TOKEN_COOKIE.equals(cookieName)) {
            if (cookieValue.length() <= 8) {
                return "****";
            }
            return cookieValue.substring(0, 4) + "****" + cookieValue.substring(cookieValue.length() - 4);
        }

        // For non-sensitive cookies, return the full value
        return cookieValue;
    }

}
