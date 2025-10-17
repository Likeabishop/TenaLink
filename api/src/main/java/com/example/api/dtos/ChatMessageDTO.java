package com.example.api.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.api.entities.enums.MessageType;

public class ChatMessageDTO {
    private UUID messageId;
    private String content;
    private MessageType messageType;
    private UserDTO sender;
    private LocalDateTime sentAt;
    private boolean isRead;
}
