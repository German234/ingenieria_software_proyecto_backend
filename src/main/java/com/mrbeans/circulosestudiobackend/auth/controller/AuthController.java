package com.mrbeans.circulosestudiobackend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrbeans.circulosestudiobackend.auth.dtos.AuthRequest;
import com.mrbeans.circulosestudiobackend.auth.dtos.AuthResponse;
import com.mrbeans.circulosestudiobackend.auth.dtos.UserInfoDto;
import com.mrbeans.circulosestudiobackend.common.dto.SuccessResponse;
import com.mrbeans.circulosestudiobackend.keycloak.annotations.PublicEndpoint;
import com.mrbeans.circulosestudiobackend.keycloak.annotations.Scopes;
import com.mrbeans.circulosestudiobackend.keycloak.client.iKeycloakAuthClient;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.UserInfoDTO;
import com.mrbeans.circulosestudiobackend.security.CustomUserPrincipal;
import com.mrbeans.circulosestudiobackend.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final String scope = "openid profile email offline_access";
    private final iKeycloakAuthClient keycloakAdminClient;
    private AuthenticationManager authManager;
    private JwtTokenProvider tokenProvider;
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

    public AuthController(iKeycloakAuthClient keycloakAdminClient, JwtTokenProvider tokenProvider, AuthenticationManager authManager) {
        this.keycloakAdminClient = keycloakAdminClient;
        this.tokenProvider = tokenProvider;
        this.authManager = authManager;
    }

    @PublicEndpoint
    @GetMapping("/login")
    public ResponseEntity<SuccessResponse<AuthResponse>> login() {
        String authorizationUrl = UriComponentsBuilder.fromUriString(authUrl).queryParam("response_type", "code").queryParam("client_id", clientId).queryParam("redirect_uri", redirectUri).queryParam("scope", scope).build().toUriString();
        return ResponseEntity.status(302).header(HttpHeaders.LOCATION, authorizationUrl).build();
    }
    @PublicEndpoint
    @Scopes(name = "offline_access")
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code, HttpServletResponse resp) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=authorization_code" + "&code=" + code + "&client_id=" + clientId + "&client_secret=" + clientSecret + "&redirect_uri=" + redirectUri;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> tokenResponse = response.getBody();
            String accessToken = (String) tokenResponse.get("access_token");
            String refreshToken = (String) tokenResponse.get("refresh_token");

            resp.addHeader("Set-Cookie", "access_token=" + accessToken + "; Domain=.fopinet.com; Path=/; Max-Age=3600; HttpOnly; Secure; SameSite=None");
//            resp.addHeader("Set-Cookie", "access_token=" + accessToken + "; Path=/; Max-Age=3600; HttpOnly; SameSite=Lax");


            if (refreshToken != null) {
                resp.addHeader("Set-Cookie", "refresh_token=" + refreshToken + "; Domain=.fopinet.com; Path=/; Max-Age=604800; HttpOnly; Secure; SameSite=None");
//                resp.addHeader("Set-Cookie", "refresh_token=" + refreshToken + "; Path=/; Max-Age=604800; HttpOnly; SameSite=Lax");

            }
            Map<String, String> responseBody = Map.of("data", "Login exitoso");
            return ResponseEntity.ok().body(responseBody);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body("Error al obtener token");
        }
    }

    @Scopes(name = "me")
    @GetMapping("/me")
    public UserInfoDTO me(@RequestHeader("Authorization") String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        log.info("Token: {}", accessToken);

        String token = accessToken.substring(7); // remove "Bearer "
        headers.setBearerAuth(token);

        // ======== DECODIFICAR EL TOKEN JWT ========
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        String username = null;
        String email = null;
        String id = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> tokenData = mapper.readValue(payload, Map.class);
            id = (String) tokenData.get("sub");
            username = (String) tokenData.get("name");
            email = (String) tokenData.get("email");
        } catch (Exception e) {
            log.error("Error decoding JWT: {}", e.getMessage());
            return null;
        }

        // ========== OBTENER PERMISOS UMA ==========
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "urn:ietf:params:oauth:grant-type:uma-ticket");
        form.add("audience", clientId);
        form.add("response_mode", "permissions");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    List.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
                log.info("Response body: {}", response);
                List<Map<String, Object>> permissions = response.getBody();

                List<String> permisos = permissions.stream()
                        .filter(permission -> permission.get("scopes") instanceof List)
                        .flatMap(p -> ((List<String>) p.get("scopes")).stream())
                        .distinct()
                        .collect(Collectors.toList());

                return new UserInfoDTO(id, username, email, permisos);
            }
        } catch (Exception e) {
            log.error("Error fetching permissions from Keycloak: {}", e.getMessage());
        }

        return null;
    }

    @GetMapping("/logout")
    @PublicEndpoint
    @Scopes(name = "offline_access")
    public ResponseEntity<String> keycloakLogout(
            HttpServletRequest req,
            HttpServletResponse response
    ) {
        String refreshToken = req.getHeader("Cookie");
        refreshToken = refreshToken.split("=")[1];
        refreshToken = refreshToken.split(";")[0];
        log.info(refreshToken);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        // Elimina todas las cookies del frontend
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(0)
                .path("/")
                .domain(".fopinet.com")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        try {
            String logoutUrl = baseUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("client_id", clientId);
            form.add("client_secret", clientSecret);
            form.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

            try {
                ResponseEntity<String> res = restTemplate.postForEntity(logoutUrl, request, String.class);
                if (res.getStatusCode().is2xxSuccessful()) {
                    return ResponseEntity.ok("Successfully logged out from Keycloak.");
                } else {
                    log.warn("Logout from Keycloak returned non-success status: {}", res.getStatusCode());
                    return ResponseEntity.ok("Logout request failed.");
                }
            } catch (Exception e) {
                log.error("Error logging out from Keycloak", e);
                throw new RuntimeException("Failed to logout from Keycloak");
            }
        } catch (Exception e) {
            log.error("Error logging out from Keycloak", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to logout from Keycloak");
        }
    }
}