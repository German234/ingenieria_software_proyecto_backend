package com.mrbeans.circulosestudiobackend.auth.controller;

import com.mrbeans.circulosestudiobackend.auth.service.JwtTokenProcessor;
import com.mrbeans.circulosestudiobackend.auth.service.TokenExtractor;
import com.mrbeans.circulosestudiobackend.common.dto.SuccessResponse;
import com.mrbeans.circulosestudiobackend.keycloak.annotations.PublicEndpoint;
import com.mrbeans.circulosestudiobackend.keycloak.annotations.Scopes;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.UserInfoDTO;
import com.mrbeans.circulosestudiobackend.keycloak.service.KeycloakService;
import com.mrbeans.circulosestudiobackend.user.repositories.IUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProcessor jwtTokenProcessor;
    private final TokenExtractor tokenExtractor;
    private final KeycloakService keycloakService;
    private final IUserRepository userRepository;

    @Autowired
    public AuthController(JwtTokenProcessor jwtTokenProcessor,
                          TokenExtractor tokenExtractor,
                          KeycloakService keycloakService,
                          IUserRepository userRepository) {
        this.jwtTokenProcessor = jwtTokenProcessor;
        this.tokenExtractor = tokenExtractor;
        this.keycloakService = keycloakService;
        this.userRepository = userRepository;
    }

    @PublicEndpoint
    @GetMapping("/login")
    public ResponseEntity<SuccessResponse<String>> login() {
        String authorizationUrl = keycloakService.generateAuthorizationUrl();
        return ResponseEntity.ok().body(new SuccessResponse<String>("Authorization URL generated successfully", authorizationUrl));
    }

    @PublicEndpoint
    @Scopes(name = "offline_access")
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code, HttpServletResponse resp) {
        // Exchange authorization code for tokens using KeycloakService
        ResponseEntity<com.mrbeans.circulosestudiobackend.keycloak.dtos.KeycloakTokenResponse> tokenResponse =
            keycloakService.exchangeCodeForTokens(code, resp);

        if (tokenResponse.getStatusCode().is2xxSuccessful() && tokenResponse.getBody() != null) {
            String accessToken = tokenResponse.getBody().getAccessToken();
            
            // Extract user email from JWT token using JwtTokenProcessor
            String email = jwtTokenProcessor.extractEmailFromToken(accessToken);

            // Validate if user exists in local database
            if (email == null || !userRepository.existsByEmail(email)) {
                log.info("User not found with email {}", email);
                log.info("Emails existing in the system: {}", userRepository.findAll().stream().map(u -> u.getEmail()).collect(Collectors.toList()));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no registrado en el sistema");
            }

            Map<String, String> responseBody = Map.of("data", "Login exitoso");
            return ResponseEntity.ok().body(responseBody);
        } else {
            return ResponseEntity.status(tokenResponse.getStatusCode()).body("Error al obtener token");
        }
    }

    @Scopes(name = "me")
    @GetMapping("/me")
    public UserInfoDTO me(@RequestHeader(value = "Authorization", required = false) String accessToken,
                          HttpServletRequest request) {
        
        // Use TokenExtractor to get token with priority order
        TokenExtractor.TokenExtractionResult extractionResult = tokenExtractor.extractTokenWithPriority(request);
        
        // If token is found in Authorization header or cookies
        if (extractionResult.hasToken()) {
            String token = extractionResult.getToken().get();
            log.info("Using token from {}", extractionResult.getSource().getDescription());
            log.info("Token: {}", jwtTokenProcessor.maskToken(token));
            
            // Use KeycloakService to extract user information including permissions
            return keycloakService.extractUserInfo(token);
        }
        
        // If authentication is found in SecurityContext
        if (extractionResult.hasAuthentication()) {
            Authentication authentication = extractionResult.getAuthentication().get();
            
            // Create a UserInfoDTO from the authenticated user
            String email = authentication.getName();
            String username = authentication.getName(); // Using email as username fallback
            String id = authentication.getName(); // Using email as ID fallback
            
            // Get authorities/permissions from authentication
            List<String> permisos = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            
            log.info("Using authentication from SecurityContext for user: {}", email);
            return new UserInfoDTO(id, username, email, permisos);
        }
        
        log.error("No authentication token found in header, cookies, or SecurityContext");
        return null;
    }

    @GetMapping("/logout")
    @PublicEndpoint
    @Scopes(name = "offline_access")
    public ResponseEntity<String> keycloakLogout(
            HttpServletRequest req,
            HttpServletResponse response
    ) {
        try {
            // Extract refresh token from cookies
            String refreshToken = extractRefreshTokenFromCookies(req)
                    .orElseThrow(() -> new RuntimeException("No refresh token found in cookies"));
            
            log.info("Logging out with refresh token: {}", jwtTokenProcessor.maskToken(refreshToken));
            
            // Use KeycloakService to handle logout
            ResponseEntity<String> logoutResponse = keycloakService.logout(refreshToken);
            
            // Clear cookies by setting them with max-age=0
            response.addHeader("Set-Cookie", "access_token=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax");
            response.addHeader("Set-Cookie", "refresh_token=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax");
            
            return logoutResponse;
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to logout from Keycloak");
        }
    }

    /**
     * Extract refresh token from cookies
     */
    private java.util.Optional<String> extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return java.util.Arrays.stream(request.getCookies())
                    .filter(cookie -> "refresh_token".equals(cookie.getName()))
                    .map(jakarta.servlet.http.Cookie::getValue)
                    .findFirst();
        }
        return java.util.Optional.empty();
    }
}