package com.mrbeans.circulosestudiobackend.keycloak.security;

import com.mrbeans.circulosestudiobackend.keycloak.annotations.PublicEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PublicEndpointsRegistry implements InitializingBean {

    private final RequestMappingHandlerMapping handlerMapping;

    private final Set<Endpoint> publicEndpoints = ConcurrentHashMap.newKeySet();

    public PublicEndpointsRegistry(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            RequestMappingInfo mappingInfo = entry.getKey();

            boolean isPublic = handlerMethod.hasMethodAnnotation(PublicEndpoint.class) ||
                    handlerMethod.getBeanType().isAnnotationPresent(PublicEndpoint.class);

            if (isPublic) {
                Set<RequestMethod> methods = mappingInfo.getMethodsCondition().getMethods();
                Set<String> patterns = mappingInfo.getPathPatternsCondition()
                        .getPatterns()
                        .stream()
                        .map(PathPattern::getPatternString)
                        .collect(Collectors.toSet());

                if (methods.isEmpty()) {
                    for (String pattern : patterns) {
                        publicEndpoints.add(new Endpoint("ALL", pattern));
                        log.info("[PublicEndpointsRegistry] Registrado endpoint público: método=ALL, ruta={}", pattern);
                    }
                } else {
                    for (RequestMethod method : methods) {
                        for (String pattern : patterns) {
                            publicEndpoints.add(new Endpoint(method.name(), pattern));
                            log.info("[PublicEndpointsRegistry] Registrado endpoint público: método={}, ruta={}", method.name(), pattern);
                        }
                    }
                }
            }
        }
    }

    public boolean isPublicEndpoint(String httpMethod, String path) {
        if(Objects.equals(httpMethod, HttpMethod.OPTIONS.name())) return true;
        return publicEndpoints.stream().anyMatch(ep -> ep.matches(httpMethod, path));
    }

    public Set<String> getPublicEndpoints() {
        Set<String> paths = new HashSet<>();
        for (Endpoint ep : publicEndpoints) {
            paths.add(ep.pathPattern);
        }
        return paths;
    }

    private static class Endpoint {
        private final String httpMethod;
        private final String pathPattern;

        public Endpoint(String httpMethod, String pathPattern) {
            this.httpMethod = httpMethod;
            this.pathPattern = pathPattern;
        }

        public boolean matches(String method, String path) {
            if (!this.httpMethod.equalsIgnoreCase("ALL") && !this.httpMethod.equalsIgnoreCase(method)) {
                return false;
            }
            AntPathMatcher matcher = new AntPathMatcher();
            return matcher.match(this.pathPattern, path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(httpMethod.toUpperCase(), pathPattern);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Endpoint other)) return false;
            return this.httpMethod.equalsIgnoreCase(other.httpMethod)
                    && this.pathPattern.equals(other.pathPattern);
        }
    }
}
