package com.mrbeans.circulosestudiobackend.keycloak.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Data
public class KeycloakProperties {
    private String serverUrl;
    private String realm;
    private KeycloakClientProperties client;
    private KeycloakBaseProperties base;
}

@Data
class KeycloakBaseProperties {
    private String url;
}

@Data
class KeycloakClientProperties {
    private String id;
    private String secret;
}
