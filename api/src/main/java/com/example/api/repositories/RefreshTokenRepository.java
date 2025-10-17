package com.example.api.repositories;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import com.example.api.entities.RefreshToken;
import com.example.api.entities.User;
import com.example.api.entities.enums.UserStatus;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {
    RefreshToken findByToken(String refreshToken);
    RefreshToken findByUser(User user);
    void deleteByUser(User user);
    void deleteByExpiryDateBefore(LocalDateTime date);
}
