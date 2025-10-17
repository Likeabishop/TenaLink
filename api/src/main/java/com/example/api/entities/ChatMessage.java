package com.example.api.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.api.entities.enums.MessageType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue
    private UUID messageId;

    @ManyToOne
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    private MessageType messageType;
    private String fileUrl; // for attachments

    private LocalDateTime sentAt;
    private boolean isRead;
}
