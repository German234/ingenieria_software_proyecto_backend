package com.mrbeans.circulosestudiobackend.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CorsConfig {
    private final CorsProperties props;

    public CorsConfig(CorsProperties props) {
        this.props = props;
        // Log the CORS configuration on startup
        log.info("CORS Configuration - Allowed Origins: {}", props.getAllowedOrigins());
        log.info("CORS Configuration - Allowed Methods: {}", props.getAllowedMethods());
        log.info("CORS Configuration - Allowed Headers: {}", props.getAllowedHeaders());
        log.info("CORS Configuration - Allow Credentials: {}", props.isAllowCredentials());
        log.info("CORS is now configured through SecurityConfig for proper integration with Spring Security");
    }
}
