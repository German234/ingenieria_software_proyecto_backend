package com.mrbeans.circulosestudiobackend.common.filter;

import com.mrbeans.circulosestudiobackend.keycloak.security.PublicEndpointsRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
public class PublicEndpointFilter extends OncePerRequestFilter {

    private final PublicEndpointsRegistry publicEndpointsRegistry;

    public PublicEndpointFilter(PublicEndpointsRegistry publicEndpointsRegistry) {
        this.publicEndpointsRegistry = publicEndpointsRegistry;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Skip all processing for OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("Skipping public endpoint processing for OPTIONS request");
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (!contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        String method = request.getMethod();
        log.info("Path: {} Method: {} Public: {}", path, method, publicEndpointsRegistry.isPublicEndpoint(method, path));
        if (publicEndpointsRegistry.isPublicEndpoint(method, path)) {
            log.info("Public endpoint detected: {} {}. Setting anonymous authentication", method, path);
            AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
                    "public",
                    "anonymousUser",
                    AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
            );
            SecurityContextHolder.getContext().setAuthentication(anonymousToken);
            log.info("Anonymous authentication set for public endpoint: {} {}", method, path);
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}

