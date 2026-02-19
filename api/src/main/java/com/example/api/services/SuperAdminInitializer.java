package com.example.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.api.entities.User;
import com.example.api.entities.enums.Role;
import com.example.api.entities.enums.UserStatus;
import com.example.api.repositories.UserRepository;

@Service
public class SuperAdminInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String superPassword;
    private final String superEmail;
    private final String appName;

    public SuperAdminInitializer(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        @Value("app.prod.super-password") String superPassword,
        @Value("app.super-email") String superEmail,
        @Value("spring.application.name") String appName
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.superPassword = superPassword;
        this.superEmail = superEmail;
        this.appName = appName;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeSuperAdmin() {
        // Check if super admin already exists
        if (userRepository.existsByRole(Role.SUPER_ADMIN)) {
            return;
        }

        // Create initial super admin (you)
        User superAdmin = new User();
        superAdmin.setEmail(superEmail);
        superAdmin.setPassword(passwordEncoder.encode(superPassword));
        superAdmin.setFirstName(appName);
        superAdmin.setLastName(Role.SUPER_ADMIN.toString());
        superAdmin.setRole(Role.SUPER_ADMIN);
        superAdmin.setStatus(UserStatus.IS_ACTIVE);
        superAdmin.setCreatedBy("system");
        superAdmin.setIdentificationNumber("0000000000000");

        userRepository.save(superAdmin);
    }
}
