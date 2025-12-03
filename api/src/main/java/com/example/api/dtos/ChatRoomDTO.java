package com.example.api.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.api.entities.User;
import com.example.api.entities.enums.ChatRoomType;

import lombok.Data;

@Data
public class ChatRoomDTO {
    private UUID roomId;
    private String name;
    private ChatRoomType type;
    private Long unreadCount;
    private LocalDateTime lastActivity;
    private Set<User> participants;

    public ChatRoomDTO(
        UUID roomId,
        String name,
        ChatRoomType type,
        Set<User> participants
    ) {
        this.roomId = roomId;
        this.name = name;
        this.type = type;
        this.participants = participants;
    }

}
