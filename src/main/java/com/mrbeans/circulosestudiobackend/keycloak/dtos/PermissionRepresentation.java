package com.mrbeans.circulosestudiobackend.keycloak.dtos;

import lombok.Data;

import java.util.List;

@Data
public class PermissionRepresentation {
    private String decisionStrategy;
    private String description;
    private String id;
    private String logic;
    private String name;
    private String resourceType;
    private String type;
    private List<String> scopes;

}
