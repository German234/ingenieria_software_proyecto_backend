package com.mrbeans.circulosestudiobackend.keycloak.dtos;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class keycloakClientResponse {
    private String id;
    private String clientId;
    private String name;
    private String description;
    private String rootUrl;
    private String adminUrl;
    private String baseUrl;
    private boolean surrogateAuthRequired;
    private boolean enabled;
    private boolean alwaysDisplayInConsole;
    private String clientAuthenticatorType;
    private String secret;
    private List<String> redirectUris;
    private List<String> webOrigins;
    private int notBefore;
    private boolean bearerOnly;
    private boolean consentRequired;
    private boolean standardFlowEnabled;
    private boolean implicitFlowEnabled;
    private boolean directAccessGrantsEnabled;
    private boolean serviceAccountsEnabled;
    private boolean authorizationServicesEnabled;
    private boolean publicClient;
    private boolean frontchannelLogout;
    private String protocol;
    private Map<String, String> attributes;
    private Map<String, String> authenticationFlowBindingOverrides;
    private boolean fullScopeAllowed;
    private int nodeReRegistrationTimeout;
    private List<String> defaultClientScopes;
    private List<String> optionalClientScopes;
}