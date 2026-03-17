package com.tsms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * CorsConfig — Enables Cross-Origin Resource Sharing
 * This allows our frontend HTML files (served from file:// or a different port)
 * to call the Spring Boot REST APIs without browser CORS errors.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")           // Apply to all /api/ endpoints
                .allowedOrigins("*")             // Allow any origin (frontend URL)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
