package com.example.api.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.dtos.ChatMessageDTO;
import com.example.api.dtos.ChatRoomDTO;
import com.example.api.dtos.SendMessageRequest;
import com.example.api.entities.ChatMessage;
import com.example.api.entities.User;
import com.example.api.entities.enums.MessageType;
import com.example.api.services.ChatService;

@RestController
@RequestMapping("chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/rooms")
    public List<ChatRoomDTO> getUserChatRooms(
        @AuthenticationPrincipal User user) {
            return chatService.getUserChatRooms(user.getUserId());
    }

    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessageDTO> getRoomMessages(
        @PathVariable UUID roomId,
        @AuthenticationPrincipal User user) {
            return chatService.getRoomMessages(roomId, user.getUserId());
    }
    
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessageDTO sendMessage(
        @DestinationVariable UUID roomId,
        @RequestBody SendMessageRequest request,
        @AuthenticationPrincipal User user) {

            ChatMessage message = chatService.sendMessage(
                roomId, 
                user.getUserId(), 
                request.getContent(), 
                MessageType.TEXT);

            return new ChatMessageDTO(
                message.getMessageId(), 
                message.getContent(), 
                message.getMessageType(), 
                message.getSender(), 
                message.getCreatedDate(), 
                message.isRead()
            );
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ChatMessageDTO sendMessageViaRest(
        @PathVariable UUID roomId,
        @RequestBody SendMessageRequest request,
        @AuthenticationPrincipal User user) {
            ChatMessage message = chatService.sendMessage(
                roomId, 
                user.getUserId(), 
                request.getContent(), 
                MessageType.TEXT
            );

            return new ChatMessageDTO(
                message.getMessageId(), 
                message.getContent(), 
                message.getMessageType(), 
                message.getSender(), 
                message.getCreatedDate(), 
                message.isRead()
            );
        }
}
