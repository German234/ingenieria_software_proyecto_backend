package com.mrbeans.circulosestudiobackend.auth.controller;

import com.mrbeans.circulosestudiobackend.auth.dtos.AuthRequest;
import com.mrbeans.circulosestudiobackend.auth.dtos.AuthResponse;
import com.mrbeans.circulosestudiobackend.auth.dtos.UserInfoDto;
import com.mrbeans.circulosestudiobackend.common.dto.SuccessResponse;
import com.mrbeans.circulosestudiobackend.security.CustomUserPrincipal;
import com.mrbeans.circulosestudiobackend.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtTokenProvider tokenProvider;


    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        String jwt = tokenProvider.generateToken(auth);

        UserInfoDto userInfo = new UserInfoDto(
                principal.getId(),
                principal.getName(),
                principal.getEmail(),
                principal.getRoleName(),
                principal.getImageUrl(),
                principal.isActive()
        );

        AuthResponse resp = new AuthResponse(
                jwt,
                userInfo
        );

        SuccessResponse<AuthResponse> successResponse = new SuccessResponse<>(HttpStatus.OK.value(),"Inicio de sesion correcto", resp);
        return ResponseEntity.ok(successResponse);
    }
}