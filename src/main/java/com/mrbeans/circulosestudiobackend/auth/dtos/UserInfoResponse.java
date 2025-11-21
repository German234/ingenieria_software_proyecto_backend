package com.mrbeans.circulosestudiobackend.auth.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private String id;
    private String nombreCompleto;
    private String email;
    private String image;
    
    @JsonProperty("isActive")
    private boolean isActive;
    
    private String role;
}