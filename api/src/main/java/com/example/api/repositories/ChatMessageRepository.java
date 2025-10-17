package com.example.api.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.api.entities.ChatMessage;
import com.example.api.entities.ChatRoom;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID>{
    List<ChatMessage> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.roomId = :roomId ORDER BY cm.sentAt DESC")
    List<ChatMessage> findLatestMessages(@Param("roomId") UUID roomId, Pageable pageable);

    // Count unread messages for a user in a room
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.roomId = : roomId AND cm.sender.userId != :userId AND cm.isRead = false")
    long countUnreadMessage(@Param("roomId") UUID roomId, @Param("userId") UUID userId);
}
