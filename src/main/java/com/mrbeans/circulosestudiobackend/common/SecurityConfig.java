package com.mrbeans.circulosestudiobackend.common;

import com.mrbeans.circulosestudiobackend.common.config.CorsProperties;
import com.mrbeans.circulosestudiobackend.common.config.JwtConverterImpl;
import com.mrbeans.circulosestudiobackend.common.filter.CookieTokenFilter;
import com.mrbeans.circulosestudiobackend.common.filter.PublicEndpointFilter;
import com.mrbeans.circulosestudiobackend.keycloak.security.PublicEndpointsRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final PublicEndpointsRegistry publicEndpointsRegistry;
    private final CorsProperties corsProperties;
    private final CookieTokenFilter cookieTokenFilter;
    private final PublicEndpointFilter publicEndpointFilter;

    public SecurityConfig(PublicEndpointsRegistry publicEndpointsRegistry,
                          CorsProperties corsProperties,
                          CookieTokenFilter cookieTokenFilter,
                          PublicEndpointFilter publicEndpointFilter) {
        this.publicEndpointsRegistry = publicEndpointsRegistry;
        this.corsProperties = corsProperties;
        this.cookieTokenFilter = cookieTokenFilter;
        this.publicEndpointFilter = publicEndpointFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   Converter<Jwt, AbstractAuthenticationToken> jwtConverter,
                                                   HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        
        // Log CORS configuration for debugging
        log.info("Configuring CORS with origins: {}", corsProperties.getAllowedOrigins());
        
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                // Note: CorsFilter is automatically registered with @Order(Ordered.HIGHEST_PRECEDENCE)
                // so it runs before all other filters including authentication filters
                .addFilterBefore(publicEndpointFilter, BearerTokenAuthenticationFilter.class)
                .addFilterBefore(cookieTokenFilter, BearerTokenAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS requests are handled by CorsFilter, but we also permit them here
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.OPTIONS, "/**")).permitAll()
                        .requestMatchers(publicEndpointsRegistry.getPublicEndpoints().toArray(new String[0]))
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtConverter(
            @Value("${jwt.auth.converter.principle-attribute:sub}") String principle,
            @Value("${jwt.auth.converter.resource-id:}") String resourceId
    ) {
        return new JwtConverterImpl(principle, resourceId);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withIssuerLocation("https://auth.fopinet.com/realms/capas")
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("Creating CorsConfigurationSource with origins: {}", corsProperties.getAllowedOrigins());
        
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins
        List<String> allowedOrigins = corsProperties.getAllowedOrigins();
        configuration.setAllowedOrigins(allowedOrigins);
        log.info("CORS allowed origins set to: {}", allowedOrigins);
        
        // Set allowed methods
        List<String> allowedMethods = corsProperties.getAllowedMethods();
        configuration.setAllowedMethods(allowedMethods);
        log.info("CORS allowed methods set to: {}", allowedMethods);
        
        // Set allowed headers - include common headers and allow dynamic headers
        List<String> allowedHeaders = corsProperties.getAllowedHeaders();
        if (allowedHeaders.contains("*")) {
            // For wildcard, set common headers and allow dynamic headers
            configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Cache-Control",
                "X-Auth-Token",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
            ));
            configuration.setAllowCredentials(true);
        } else {
            configuration.setAllowedHeaders(allowedHeaders);
        }
        log.info("CORS allowed headers set to: {}", configuration.getAllowedHeaders());
        
        // Set allow credentials
        configuration.setAllowCredentials(corsProperties.isAllowCredentials());
        log.info("CORS allow credentials set to: {}", corsProperties.isAllowCredentials());
        
        // Set exposed headers if needed
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Set max age from properties
        configuration.setMaxAge(corsProperties.getMaxAge());
        
        // Apply configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        log.info("CorsConfigurationSource created successfully");
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
