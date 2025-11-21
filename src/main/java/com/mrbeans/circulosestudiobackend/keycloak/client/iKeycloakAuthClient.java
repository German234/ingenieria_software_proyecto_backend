package com.mrbeans.circulosestudiobackend.keycloak.client;

import com.mrbeans.circulosestudiobackend.keycloak.config.KeycloakAuthFeignConfig;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.KeycloakTokenResponse;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.UMAResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "keycloak-service", url = "${keycloak.base.url}", configuration = KeycloakAuthFeignConfig.class)
public interface iKeycloakAuthClient {
    @PostMapping(value = "/realms/${keycloak.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KeycloakTokenResponse getToken(@RequestBody MultiValueMap<String, String> formData);

    @PostMapping(value = "/realms/${keycloak.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    List<UMAResponse> requestPermissionTicket(@RequestHeader("Authorization") String accessToken, @RequestBody MultiValueMap<String, String> formData);
}
