package com.example.api.entities;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.example.api.entities.enums.ChatRoomType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class ChatRoom {
    @Id
    @GeneratedValue
    private UUID roomId;
    
    private String name;
    private ChatRoomType type;

    @ManyToMany
    @JoinTable(
        name = "chat_room_users",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id") 
    )
    private Set<User> participants = new HashSet<>();
}
