package com.example.api.services;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.api.dtos.NewPasswordDTO;
import com.example.api.entities.RefreshToken;
import com.example.api.entities.User;
import com.example.api.entities.enums.Role;
import com.example.api.entities.enums.UserStatus;
import com.example.api.exceptions.ResourceNotFoundException;
import com.example.api.repositories.RefreshTokenRepository;
import com.example.api.repositories.UserRepository;
import com.example.api.utils.TokenUtils;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final String secret;
    private final String apiUrl;
    private final String clientUrl;

    public AuthService(
        UserRepository userRepository,
        RefreshTokenRepository refreshTokenRepository,
        PasswordEncoder passwordEncoder,
        @Value("${app.jwt.secret}") String secret,
        EmailService emailService,
        @Value("${app.api-url}") String apiUrl,
        @Value("${app.client-url}") String clientUrl
    ) {
        this.passwordEncoder = passwordEncoder;
        this.secret = secret;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailService = emailService;
        this.apiUrl = apiUrl;
        this.clientUrl = clientUrl;
    }

    @Transactional
    public User createUser(User user) throws MessagingException {
        User existingUser = getUser(user.getEmail());

        if (existingUser != null) {
            throw new RuntimeException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationToken(TokenUtils.generateToken());
        user.setVerificationTokenExpiry(TokenUtils.generateExpiryDate());

        sendEmailToClient(
            user.getEmail(),
            user.getName() + " " + user.getSurname(),
            "Verification for New User Email",
            "first_time_verify",
            apiUrl + "/auth/verify-email?verificationToken=" + user.getVerificationToken());
        return userRepository.save(user);
    }

    @Transactional
    public User registerClientAdmin(User user) throws MessagingException {
        User existingUser = getUser(user.getEmail());

        if (existingUser != null) {
            throw new RuntimeException("User already exists");
        }

        user.setRole(Role.ADMIN);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationToken(TokenUtils.generateToken());
        user.setVerificationTokenExpiry(TokenUtils.generateExpiryDate());
        user.setStatus(UserStatus.IS_UNACTIVE); // New status

        User savedUser = userRepository.save(user);

        sendEmailToClient(
            user.getEmail(),
            user.getName() + " " + user.getSurname(),
            "Verification for New User Email",
            "one-time_verify",
            apiUrl + "/auth/verify-email?verificationToken=" + user.getVerificationToken());
        
        notifySuperAdminNewClient(savedUser);
        return savedUser;
    }

    @Transactional
    public Map<String, String> login(String email, String rawPassword) {
        System.out.println("email: " + email);
        User user = getUser(email);

        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword()) || user == null) {
            throw new RuntimeException("Invalid credentials");
        }

        if(user.getStatus().equals(UserStatus.IS_UNACTIVE)) {
            throw new RuntimeException("Account is not verified, please verify or contact administrator!");
        } 

        if(user.getStatus().equals(UserStatus.SUSPENDED)) {
            throw new RuntimeException("Account has been suspended, contact administrator!");
        } 

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        return Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken,
            "tokenType", "Bearer",
            "expiresIn", "3600"
        );
    }

    @Transactional
    public ResponseEntity<Void> verifyEmail(String verificationToken) throws BadRequestException {
        User user = userRepository.findByVerificationToken(verificationToken);

        if (user == null) {
            throw new ResourceNotFoundException("Invalid verification token");
        }

        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification token has expired");
        }

        user.setStatus(UserStatus.IS_ACTIVE);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<Void> requestPasswordReset(String email) throws MessagingException {
        User user = getUser(email);

        if (user == null) {
            throw new ResourceNotFoundException("Email does not exist");
        }

        user.setResetPasswordToken(TokenUtils.generateToken());
        user.setResetPasswordTokenExpiry(TokenUtils.generateExpiryDate());
        User savedUser = userRepository.save(user);

        sendEmailToClient(
            savedUser.getEmail(),
            savedUser.getName() + " " + savedUser.getSurname(),
            "Reset Password Request",
            "password_reset",
            clientUrl + "/reset-password?resetPasswordToken=" + user.getResetPasswordToken()
        );

        return ResponseEntity.noContent().build(); 
    }

    @Transactional
    public ResponseEntity<Void> resetPassword(
        NewPasswordDTO newPasswordDTO,
        String resetPasswordToken) throws BadRequestException {
            User user = userRepository.findByResetPasswordToken(resetPasswordToken);
            System.out.println("newPasswordDTO: " + newPasswordDTO.getPassword());
            System.out.println("resetPasswordToken: " + resetPasswordToken);
            if (user == null) {
                throw new ResourceNotFoundException("Invalid password reset token");
            }

            if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Password reset token has expired");
            }

            user.setPassword(passwordEncoder.encode(newPasswordDTO.getPassword()));
            user.setResetPasswordToken(null);
            user.setResetPasswordTokenExpiry(null);
            userRepository.save(user);

            return ResponseEntity.noContent().build(); 
    }

    @Transactional
    public Map<String, String> refreshToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken);

        if (storedToken == null) {
            throw new RuntimeException("Refresh token not found");
        }

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new RuntimeException("Refresh token expired");
        }

        if (storedToken.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        User user = storedToken.getUser();

        // Generate new access token
        String newAccessToken = generateAccessToken(user);
        
        String newRefeshToken = rototeRefreshToken(storedToken);

        return Map.of(
            "accessToken", newAccessToken,
            "refreshToken", newRefeshToken
        );
    }

    @Transactional
    public ResponseEntity<String> logout(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken);

        if (token != null) {
            refreshTokenRepository.delete(token);
        }

        return ResponseEntity.ok("Logged out successfully");
    }

    private String rototeRefreshToken(RefreshToken oldToken) {
        refreshTokenRepository.delete(oldToken);

        return generateRefreshToken(oldToken.getUser());
    }

    private String generateAccessToken(User user) {
        return Jwts.builder()
            .setSubject(user.getUserId().toString())
            .claim("role", user.getRole().name())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 hour
            .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)), SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken(User user) {
        String refreshToken = TokenUtils.generateToken();

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(refreshToken);
        token.setExpiryDate(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(token);
        return refreshToken;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email);
    }

    private void notifySuperAdminNewClient(User clientAdmin) throws MessagingException {
        // Get all super admins
        List<User> superAdmins = userRepository.findByRole(Role.SUPER_ADMIN);

        for (User superAdmin : superAdmins) {
            Map<String, Object> variables = Map.of(
                "clientName", clientAdmin.getName() + " " + clientAdmin.getSurname(),
                "clientEmail", clientAdmin.getEmail(),
                "businessName", clientAdmin.getName(),
                "approvalLink", apiUrl + "/super-admin/clients/" + clientAdmin.getUserId() + "/approve"
            );

            sendEmailWithVariables(
                superAdmin.getEmail(), 
                "New Client Registration - Approval Required", 
                "new_client_approval", 
                variables);
        }
    }

    private void sendEmailToClient(
            String email, 
            String username,
            String subject,
            String templateName,
            String verificationLink) throws MessagingException {

                Map<String, Object> variables = Map.of(
                    "username", username,
                    "verificationLink", verificationLink
                );
                emailService.sendEmail(
                    email, 
                    subject,
                    templateName,
                    variables);
    }

    private void sendEmailWithVariables(
            String email, 
            String subject,
            String templateName,
            Map<String, Object> variables) throws MessagingException {
                emailService.sendEmail(
                    email, 
                    subject,
                    templateName,
                    variables);
    }
}
