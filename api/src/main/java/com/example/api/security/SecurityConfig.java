package com.example.api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import com.example.api.config.WebCorsConfig;

@Configuration
public class SecurityConfig {

    private WebCorsConfig corsConfigurationSource;
    private String jwtSecret;

    public SecurityConfig(
        WebCorsConfig corsConfigurationSource,
        @Value("${app.jwt.secret}") String jwtSecret) {
            this.corsConfigurationSource = corsConfigurationSource;
            this.jwtSecret = jwtSecret;
    }

    @Bean
    public SecurityFilterChain appSecurity(
        HttpSecurity http,
        AuthenticationEntryPoint entryPoint
        ) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource.corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/auth/**").permitAll()
                .requestMatchers(
                    "/auth/register", 
                    "/auth/verify-email",
                    "/auth/request-password-reset",
                    "/auth/reset-password/**",
                    "auth/refresh-token",
                    "/auth/login").permitAll()
                .anyRequest().authenticated()            
            )
        .oauth2ResourceServer((oauth2) -> oauth2
            .authenticationEntryPoint(entryPoint)
            .jwt(Customizer.withDefaults())
        )
        .logout(logout -> logout
            .logoutUrl("/auth/logout")
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
            .permitAll());
        
        return http.build();
    }
    
    @Bean
    public JwtDecoder jwtDecoder() {
        // Base64 decoding will be handled automatically
        return NimbusJwtDecoder.withSecretKey(
            new javax.crypto.spec.SecretKeySpec(
                java.util.Base64.getDecoder().decode(jwtSecret), 
                "HmacSHA256"
            )
        ).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder(14);
    }
    
}
