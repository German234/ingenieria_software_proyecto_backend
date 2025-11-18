package com.mrbeans.circulosestudiobackend.keycloak.client;

import com.mrbeans.circulosestudiobackend.keycloak.config.KeycloakFeignInterceptorConfig;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.PermissionRepresentation;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.UserDto;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.keycloakClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "feign-admin", url = "${keycloak.base.url}", configuration = KeycloakFeignInterceptorConfig.class)
public interface iKeycloakAdminClient {
    @GetMapping("/admin/realms/${keycloak.realm}/clients?clientId=${keycloak.client.id}")
    keycloakClientResponse[] getUUIDClient();

    @GetMapping("/admin/realms/${keycloak.realm}/clients/{clientUUID}/authz/resource-server/permission")
    List<PermissionRepresentation> getPermissions(@PathVariable String clientUUID);

    @PutMapping(value = "/admin/realms/${keycloak.realm}/clients/{clientUUID}/authz/resource-server/permission/scope/{permission_id}", headers = "Content-Type=application/json")
    void updatePermission(@PathVariable String clientUUID, @PathVariable String permission_id, @RequestBody Map<String, Object> permission);

    @GetMapping("/admin/realms/${keycloak.realm}/clients/{clientUUID}/authz/resource-server/permission?name={name}")
    List<PermissionRepresentation> getPermissionByName(@PathVariable String clientUUID, @PathVariable String name);

    @GetMapping("/admin/realms/${keycloak.realm}/users/{id}")
    UserDto getUserById(@PathVariable String id);
}