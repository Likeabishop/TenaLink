package com.example.api.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class WebCorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow frontend origin to use "http://localhost:3000" for production instead of "*"
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080"));
        // Allow common methods and headers
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Origin", "Accept"));
        configuration.setAllowCredentials(true); // set to true if using cooking auth

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Apply this CORS config to all paths
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
