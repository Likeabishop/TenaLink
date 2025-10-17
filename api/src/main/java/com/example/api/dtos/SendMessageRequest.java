package com.example.api.dtos;

import com.example.api.entities.enums.MessageType;

import lombok.Data;

@Data
public class SendMessageRequest {
    private String content;
    private MessageType messageType;
}
