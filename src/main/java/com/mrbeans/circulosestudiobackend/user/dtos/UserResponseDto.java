package com.mrbeans.circulosestudiobackend.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class UserResponseDto {

    @JsonProperty("_id")
    private UUID Id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("email")
    private String email;

    @JsonProperty("documentId")
    private UUID documentId;
    @JsonProperty("image")
    private String imageUrl;

    @JsonProperty("roleName")
    private String roleName;

    @JsonProperty("isActive")
    private boolean isActive;

}
