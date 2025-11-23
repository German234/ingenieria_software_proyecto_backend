package com.mrbeans.circulosestudiobackend.keycloak.dtos;

import lombok.Data;

import java.util.List;

@Data
public class UMAResponse {
    private String rsid;
    private String rsname;
    private List<String> scopes;
}
