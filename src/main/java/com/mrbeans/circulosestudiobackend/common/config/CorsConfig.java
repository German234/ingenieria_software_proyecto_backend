package com.mrbeans.circulosestudiobackend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    private final CorsProperties props;

    public CorsConfig(CorsProperties props) {
        this.props = props;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(props.getAllowedOrigins().toArray(new String[0]))
                .allowedMethods(props.getAllowedMethods().toArray(new String[0]))
                .allowedHeaders(props.getAllowedHeaders().toArray(new String[0]))
                .allowCredentials(props.isAllowCredentials())
                .maxAge(3600);
    }
}
