package com.example.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocketSecurity
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        return messages
                // Admin-only endpoints
                .simpDestMatchers("/app/admin/**").hasRole("ADMIN")
                .simpSubscribeDestMatchers("/topic/admin/**").hasRole("ADMIN")
                
                // Chat security - different roles can access different channels
                .simpDestMatchers("/app/chat/admin/**").hasRole("ADMIN")
                .simpDestMatchers("/app/chat/tenant/**").hasAnyRole("TENANT", "ADMIN")
                .simpSubscribeDestMatchers("/topic/chat/admin/**").hasRole("ADMIN")
                .simpSubscribeDestMatchers("/topic/chat/tenant/**").hasAnyRole("TENANT", "ADMIN")
                
                // Payment security
                .simpDestMatchers("/app/payments/**").authenticated()
                .simpSubscribeDestMatchers("/topic/payments/**").authenticated()
                
                // General security
                .simpDestMatchers("/app/**").authenticated()
                .simpSubscribeDestMatchers("/topic/**").authenticated()
                .simpSubscribeDestMatchers("/user/**").authenticated()
                
                // Deny all other messages
                .anyMessage().denyAll()
                .build();
    }
}