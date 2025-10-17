package com.example.api.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.api.entities.enums.ChatRoomType;

import lombok.Data;

@Data
public class ChatRoomDTO {
    private UUID roomId;
    private String name;
    private ChatRoomType type;
    private Long unreadCount;
    private LocalDateTime lastActivity;
    private List<UserDTO> participants;
}
