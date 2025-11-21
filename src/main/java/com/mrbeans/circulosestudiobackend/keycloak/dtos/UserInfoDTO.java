package com.mrbeans.circulosestudiobackend.keycloak.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserInfoDTO {
    private String id;
    private String username;
    private String email;
    private List<String> permisos;
}