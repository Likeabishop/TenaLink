package com.example.api.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.api.entities.ChatRoom;
import com.example.api.entities.User;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    
    // Find rooms for a specific user
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.participants p WHERE p.userId = :userId")
    List<ChatRoom> findRoomsByUserId(@Param("userId") UUID userId);

    // Find property-specific tenant room
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.organization.organizationId = : orgId AND cr.type = 'PROPERTY_TENANTS'")
    Optional<ChatRoom> findPropertyTenantRoom(@Param("orgId") UUID orgId);

    // Find support room between admin and super admin
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.type = 'ADMIN_SUPPORT' AND :user MEMBER OF cr.participants")
    Optional<ChatRoom> findSupportRoomForUser(@Param("user") User user);
}
