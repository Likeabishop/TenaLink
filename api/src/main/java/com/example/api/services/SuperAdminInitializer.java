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

    public SuperAdminInitializer(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        @Value("app.prod.super-password") String superPassword
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.superPassword = superPassword;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeSuperAdmin() {
        // Check if super admin already exists
        if (userRepository.existsByRole(Role.SUPER_ADMIN)) {
            return;
        }

        // Create initial super admin (you)
        User superAdmin = new User();
        superAdmin.setEmail("karabotebeila24@gmail.com");
        superAdmin.setPassword(passwordEncoder.encode(superPassword));
        superAdmin.setName("Tenalink");
        superAdmin.setSurname("Admin");
        superAdmin.setRole(Role.SUPER_ADMIN);
        superAdmin.setStatus(UserStatus.IS_ACTIVE);

        userRepository.save(superAdmin);
    }
}
