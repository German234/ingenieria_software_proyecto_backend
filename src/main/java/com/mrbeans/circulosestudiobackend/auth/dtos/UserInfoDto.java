package com.mrbeans.circulosestudiobackend.auth.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserInfoDto {
    private UUID userId;
    private String nombreCompleto;
    private String email;
    private String role;
    private String image;

    @JsonProperty("isActive")
    private boolean isActive;
}

