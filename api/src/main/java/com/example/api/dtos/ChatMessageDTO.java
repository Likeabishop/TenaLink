package com.example.api.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.api.entities.ChatMessage;
import com.example.api.entities.User;
import com.example.api.entities.enums.MessageType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatMessageDTO {
    private UUID messageId;
    private String content;
    private MessageType messageType;
    private User sender;
    private LocalDateTime sentAt;
    private boolean isRead;

    public ChatMessageDTO(ChatMessage message) {
        this.messageId = message.getMessageId();
        this.content = message.getContent();
        this.messageType = message.getMessageType();
        this.isRead = message.isRead();
        
        // Convert User entity to UserDTO
        if (message.getSender() != null) {
            this.sender = new User();
            this.sender.setUserId(message.getSender().getUserId());
            this.sender.setFirstName(message.getSender().getFirstName());
            this.sender.setEmail(message.getSender().getEmail());
            this.sender.setRole(message.getSender().getRole());
            // Add other user fields as needed
        }
    }
}
