package com.mrbeans.circulosestudiobackend.common.config;

import com.mrbeans.circulosestudiobackend.security.CustomUserPrincipalArgumentResolver;
import com.mrbeans.circulosestudiobackend.user.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.upload-dir}")
    private String uploadDir;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
        
        // Also handle the /api/uploads/** pattern to match security configuration
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomUserPrincipalArgumentResolver(userRepository));
    }
}