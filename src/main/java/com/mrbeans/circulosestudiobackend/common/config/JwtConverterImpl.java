package com.mrbeans.circulosestudiobackend.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class JwtConverterImpl implements Converter<Jwt, AbstractAuthenticationToken> {
    private final String principleAttribute;
    private final String resourceId;
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    public JwtConverterImpl(String principleAttribute, String resourceId) {
        this.principleAttribute = principleAttribute;
        this.resourceId = resourceId;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        String principal = jwt.getClaim(principleAttribute);
        if (principal == null) {
            principal = jwt.getSubject();
        }

        return new JwtAuthenticationToken(jwt, authorities, principal);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> access = jwt.getClaim("resource_access");
        if (access == null || !access.containsKey(resourceId)) return Set.of();

        var roles = (Collection<String>) ((Map<String, Object>) access.get(resourceId)).get("roles");
        log.info("[JwtConverterImpl] Roles: {}", roles);
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toSet());
    }
}

