package com.example.api.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.api.dtos.ChatMessageDTO;
import com.example.api.dtos.ChatRoomDTO;
import com.example.api.entities.ChatMessage;
import com.example.api.entities.ChatRoom;
import com.example.api.entities.Organization;
import com.example.api.entities.User;
import com.example.api.entities.enums.ChatRoomType;
import com.example.api.entities.enums.MessageType;
import com.example.api.entities.enums.Role;
import com.example.api.exceptions.ResourceNotFoundException;
import com.example.api.repositories.ChatMessageRepository;
import com.example.api.repositories.ChatRoomRepository;
import com.example.api.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(
        ChatRoomRepository chatRoomRepository,
        ChatMessageRepository chatMessageRepository,
        UserRepository userRepository,
        SimpMessagingTemplate messagingTemplate
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // Initialize default chat rooms for new organization
    public void initializeOrganizationChatRooms(
        Organization organization,
        User admin) {
            ChatRoom tenantsRoom = new ChatRoom();
            tenantsRoom.setName(organization.getName() + " Tenants");
            tenantsRoom.setType(ChatRoomType.PROPERTY_TENANTS);
            tenantsRoom.setOrganization(organization);

            // Add admin to tenants rooms
            tenantsRoom.getParticipants().add(admin);
            chatRoomRepository.save(tenantsRoom);

            // Create support room between admin and super admins
            createAdminSupportRoom(admin);
    }
    
    private void createAdminSupportRoom(User admin) {
        ChatRoom supportRoom = new ChatRoom();
        supportRoom.setName("Support - " + admin.getName());
        supportRoom.setType(ChatRoomType.ADMIN_SUPPPORT);
        supportRoom.getParticipants().add(admin);

        // Add all super admins
        List<User> superAdmins = userRepository.findByRole(Role.SUPER_ADMIN);
        supportRoom.getParticipants().addAll(superAdmins);
        
        chatRoomRepository.save(supportRoom);
    }

    public ChatMessage sendMessage(
        UUID roomId,
        UUID senderId,
        String content,
        MessageType messageType) {
            ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Chat room not found"));

            User sender = userRepository.findById(senderId).orElseThrow(() -> new ResourceNotFoundException(
                "User not found"));

            // Check if sender if participant
            if (!room.getParticipants().contains(sender)) {
                throw new RuntimeException("User not authorized to send messages in this room");
            }
        
    
            ChatMessage message = new ChatMessage();
            message.setChatRoom(room);
            message.setSender(sender);
            message.setContent(content);
            message.setMessageType(messageType);
            message.setRead(false);
            return message;
        }

    public List<ChatRoomDTO> getUserChatRooms(UUID userId) {
        List<ChatRoom> rooms = chatRoomRepository.findRoomsByUserId(userId);
        return rooms.stream()
            .map(room -> {
                ChatRoomDTO dto = new ChatRoomDTO(
                    room.getRoomId(),
                    room.getName(),
                    room.getType(),
                    room.getParticipants()
                );
                // Add unread count for this user
                dto.setUnreadCount(chatMessageRepository.countUnreadMessage(room.getRoomId(), userId));
                return dto;
            })
            .collect(Collectors.toList());

    }

    public List<ChatMessageDTO> getRoomMessages(UUID roomId, UUID userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Chat rooom not found"));
        
        if (!room.getParticipants().stream()
            .anyMatch(user -> user.getUserId().equals(userId))) {
                throw new RuntimeException("Access denied to chat room");
            }
        
        List<ChatMessage> message = chatMessageRepository.findByChatRoomOrderByCreatedDateAsc(room);

        // Mark messages as read for this user
        message.stream()
            .filter(msg -> !msg.getSender().getUserId().equals(userId) && !msg.isRead())
            .forEach(msg -> {
                msg.setRead(true);
                chatMessageRepository.save(msg);
            });
        return message.stream()
            .map(ChatMessageDTO::new)
            .collect(Collectors.toList());
    }

    // Add tenant to property chat room when they are added to a property
    public void addUserToPropertyRoom(
        User user, 
        Organization organization) {
            ChatRoom propertyRoom = chatRoomRepository.findPropertyTenantRoom(organization.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Property chat room not found"));
            
            propertyRoom.getParticipants().add(user);
            chatRoomRepository.save(propertyRoom);

    }

    private LocalDateTime calculateLastActivity(ChatRoom room) {
        if (room.getMessages() != null && !room.getMessages().isEmpty()) {
            // Find the most recent message timestamp
            return room.getMessages().stream()
                .map(ChatMessage::getCreatedDate)
                .max(LocalDateTime::compareTo)
                .orElse(room.getCreatedDate());
        }
        return room.getCreatedDate();
    }
}
