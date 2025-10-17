package com.example.api.controllers;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.api.dtos.UserAdminUpdateDTO;
import com.example.api.dtos.UserPutDTO;
import com.example.api.dtos.UserResponseDTO;
import com.example.api.entities.User;
import com.example.api.services.UserService;

import jakarta.mail.MessagingException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }
    
    @GetMapping("/get-all")
    public ResponseEntity<List<User>> getAllUsers(
        @PageableDefault(
            page = 0, 
            size = 10, 
            sort = "createdDate",
            direction = Sort.Direction.ASC) Pageable pageable) {
        
        return userService.findAll(pageable);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<User> getUser(
        @PathVariable UUID userId) {
        
        return userService.findById(userId);
    }

    @PutMapping("/edit/{userId}")
    public ResponseEntity<Void> editUser(
        @PathVariable UUID userId,
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody UserPutDTO dto) {
            UUID loggedInUserId = getCurrentUserId();
            userService.putUser(userId, loggedInUserId, dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin-alter/{userId}")
    public ResponseEntity<Void> alterUser(
        @PathVariable UUID userId,
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody UserAdminUpdateDTO dto) throws MessagingException {
            UUID loggedInUserId = getCurrentUserId();
            userService.alterUser(userId, loggedInUserId, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> removeUser(
        @PathVariable UUID userId,
        @AuthenticationPrincipal UserDetails userDetails) throws MessagingException {
            UUID loggedInUserId = getCurrentUserId();
            return userService.deleteUser(userId, loggedInUserId);
    }

    @DeleteMapping("/suspend/{userId}")
    public ResponseEntity<Void> suspendUser(
        @PathVariable UUID userId,
        @AuthenticationPrincipal UserDetails userDetails) throws MessagingException {
            UUID loggedInUserId = getCurrentUserId();
            return userService.suspendAccount(userId, loggedInUserId);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            // Extract from subject claim (based on my JWT structure)
            String subject = jwt.getSubject();
            return UUID.fromString(subject);
        } else if (principal instanceof String) {
            return UUID.fromString((String) principal);
        } else if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            return UUID.fromString(userDetails.getUsername());
        } else {
            throw new IllegalStateException("Unknown principal type: " + principal.getClass());
        }
    }
    
}
