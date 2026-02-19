package com.example.api.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.api.dtos.UserAdminUpdateDTO;
import com.example.api.dtos.UserPutDTO;
import com.example.api.entities.User;
import com.example.api.entities.enums.Role;
import com.example.api.repositories.UserRepository;
import com.example.api.utils.TokenUtils;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.experimental.Helper;

import java.net.URI;
import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final String apiUrl;
    // private final JwtService jwtService;
    // private final AuthenticationManager authenticationManager;
    // private final UserRepository userRepository;
    // private final PasswordEncoder passwordEncoder;

    public UserService(
        UserRepository userRepository,
        EmailService emailService,
        @Value("${app.api-url}") String apiUrl
    ) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.apiUrl = apiUrl;
    }

    public ResponseEntity<List<User>> findAll(Pageable pageable) {
        Page<User> page = userRepository.findAll(
            PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "createdDate"))
            ));
        return ResponseEntity.ok(page.getContent());
    }

    public ResponseEntity<User> findById(UUID uuid) {
        User user = findUser(uuid);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional
    public User putUser(
        UUID uuid,
        UUID currentUser,
        UserPutDTO userUpdate) {
        User user = findUser(uuid);
        if (user != null) {
            user.setMiddleNames(userUpdate.getMiddleNames());
            user.setFirstName(userUpdate.getName());
            user.setUpdatedBy(currentUser.toString());

            User savedUser = userRepository.save(user);

            return savedUser;
        } else {
            return null;
        }
    }

    @Transactional
    public User alterUser(
        UUID uuid,
        UUID currentUser,
        UserAdminUpdateDTO userUpdate) throws MessagingException {
        User user = findUser(uuid);
        if (user != null) {
            user.setMiddleNames(userUpdate.getMiddleNames());
            user.setFirstName(userUpdate.getFirstName());
            user.setUpdatedBy(currentUser.toString());
            user.setEmail(userUpdate.getEmail());
            user.setIdentificationNumber(userUpdate.getIdentificationNumber());
            user.setRole(userUpdate.getRole());
            user.setLastName(userUpdate.getLastName());

            User savedUser = userRepository.save(user);
            
            sendEmail(
            user.getEmail(), 
            user.getResetPasswordToken(),
            user.getFirstName() + " " + user.getLastName(),
            "Changes Implemented on Account Profile Notice",
            "tenant_notification_of_changes");
            return savedUser;
        } else {
            return null;
        }
    }

    @Transactional
    public ResponseEntity<Void> deleteUser(
        UUID uuid,
        UUID loggedInUserId) throws MessagingException {
        System.out.println("uuid: " + uuid);
        System.out.println("userDelete: " + loggedInUserId);
        User user = findUser(uuid);

        if(user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setDeletedBy(loggedInUserId.toString());
        user.setDeletedDate(LocalDateTime.now());
        userRepository.softDelete(user);
        
        sendEmail(
            user.getEmail(), 
            user.getResetPasswordToken(),
            user.getFirstName() + " " + user.getLastName(),
            "Deleted Account Notice",
            "delete_notify"
            );
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<Void> suspendAccount(
        UUID uuid,
        UUID currentUser) throws MessagingException {
        User user = findUser(uuid);
        User suspendor = findUser(currentUser);

        if(user == null) {
            return ResponseEntity.notFound().build();
        }

        if(suspendor.getRole().equals(Role.TENANT)) {
            throw new RuntimeException("A tenant cannot have the priviledge to suspend other users");
        }

        userRepository.suspend(suspendor.getUserId(), user);
        sendEmail(
            user.getEmail(), 
            user.getResetPasswordToken(),
            user.getFirstName() + " " + user.getLastName(),
            "Suspended Account Notice",
            "suspend_notify");

        return ResponseEntity.noContent().build();
    }

    private User findUser(UUID uuid) {
        return userRepository.findByUserId(uuid);
    }

    private void sendEmail(
            String email, 
            String verificationToken, 
            String username,
            String subject,
            String templateName) throws MessagingException {

                Map<String, Object> variables = Map.of(
                    "username", username,
                    "verificationLink", apiUrl + "/auth/verify-email?verificationToken=" + verificationToken
                );
                emailService.sendEmail(
                    email, 
                    subject,
                    templateName,
                    variables);
    }

}
