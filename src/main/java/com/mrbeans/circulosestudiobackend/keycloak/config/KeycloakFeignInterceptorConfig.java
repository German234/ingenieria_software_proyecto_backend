package com.mrbeans.circulosestudiobackend.keycloak.config;

import com.mrbeans.circulosestudiobackend.keycloak.client.iKeycloakAuthClient;
import com.mrbeans.circulosestudiobackend.keycloak.dtos.KeycloakTokenResponse;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KeycloakFeignInterceptorConfig {
    // A traves de keycloakAuthClient podemos acceder al metodo getToken
    private final iKeycloakAuthClient keycloakAuthClient;
    // Obtenemos las properties asociadas en nuestro application.yml
    private final KeycloakProperties keycloakProperties;

    @Bean
    public RequestInterceptor getKeycloakAuthInterceptor() {
        return requestTemplate -> {
            log.info(requestTemplate.toString());
            if (requestTemplate.headers().containsKey("Authorization")) {
                log.info(requestTemplate.headers().get("Authorization").toString());
                log.info("Authorization header already set, skipping interceptor.");
                return;
            }
            log.info("ClientId: {}, ClientSecret: {}", keycloakProperties.getClient().getId(), keycloakProperties.getClient().getSecret());
            //devolvemos una requestTemplate con un MultiValueMap<String, String> que contendra los siguientes valores
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            // El Id de nuestro Client en Keycloak
            form.add("client_id", keycloakProperties.getClient().getId());
            // El Secret de nuestro Client en Keycloak
            form.add("client_secret", keycloakProperties.getClient().getSecret());
            //Especificamos que el tipo de entidad que se esta autenticando es un client mediante client credentials
            form.add("grant_type", "client_credentials");

            //Obtenemos un token valido para nuestro cliente
            KeycloakTokenResponse token = keycloakAuthClient.getToken(form);
            log.info("Token: {}", token);

            //Lo inyectamos en el header de nuestra peticion
            requestTemplate.header("Authorization", "Bearer " + token.getAccessToken());
        };
    }

    @Bean
    public Encoder encoder(ObjectFactory<HttpMessageConverters> converters) {
        return new SpringFormEncoder(new SpringEncoder(converters));
    }
}