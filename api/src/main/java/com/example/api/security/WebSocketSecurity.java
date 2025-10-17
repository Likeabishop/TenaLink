package com.example.api.security;

import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.stereotype.Component;

@Component
public class WebSocketSecurity implements MessageSecurityMetadataSourceRegistry {
    
    @Override
    public MessageSecurityMetadataSourceRegistry simpDestMatches() {
        return registry
            .simpDestMatchers("/chat/**").authenticated()
            .simpSubscribeDestMatchers("/topic/chat/**").authenticated();
    }
}
