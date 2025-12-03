package com.example.api.controllers;

import java.net.URI;
import java.util.Map;

import org.apache.catalina.connector.Request;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.api.dtos.LoginDTO;
import com.example.api.dtos.NewPasswordDTO;
import com.example.api.dtos.PasswordResetRequestDTO;
import com.example.api.dtos.RefreshTokenRequestDTO;
import com.example.api.dtos.UserDTO;
import com.example.api.entities.RefreshToken;
import com.example.api.entities.User;
import com.example.api.services.AuthService;
import com.example.api.services.UserService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(
        @RequestBody @Valid User user, 
        UriComponentsBuilder ucb) throws MessagingException {
            User savedUser = authService.createUser(user);

            URI locationOfNewUser = ucb
                .path("auth/register/{userId}")
                .buildAndExpand(user.getUserId())
                .toUri();
            
            UserDTO dto = convertToDTO(savedUser);

            return ResponseEntity.created(locationOfNewUser).body(dto);
    }

    @PostMapping("/login")
    public Map<String, String> login(
        @RequestBody @Valid LoginDTO LoginDTO) {
            return authService.login(LoginDTO.getEmail(), LoginDTO.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
        @RequestBody @Valid String refreshToken) {
            return authService.logout(refreshToken);
    }

    @PostMapping("/refresh-token")
    public Map<String, String> refreshToken(
        @RequestBody @Valid RefreshTokenRequestDTO refreshToken) {
            return authService.refreshToken(refreshToken.getRefreshToken());
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String verificationToken) throws BadRequestException {
        authService.verifyEmail(verificationToken);
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create("/"))
            .build();
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<Void> requestPasswordReset(
        @RequestBody @Valid PasswordResetRequestDTO passwordResetRequest) throws MessagingException {
            return authService.requestPasswordReset(passwordResetRequest.getEmail()); 
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
        @RequestBody @Valid NewPasswordDTO newPasswordDTO,
        @RequestParam String resetPasswordToken) throws BadRequestException {
            authService.resetPassword(newPasswordDTO, resetPasswordToken);
            return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("/"))
                .build(); 
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setStatus(user.getStatus());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setUserId(user.getUserId());
        dto.setMiddleNames(user.getMiddleNames());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());

        return dto;
    }

}
