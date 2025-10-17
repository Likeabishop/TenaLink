package com.example.api.entities;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.api.entities.enums.Role;
import com.example.api.entities.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name= "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "userId")
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String middleNames;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false, unique = true, length = 13)
    private String identificationNumber;

    @Column(nullable = false, unique = true) 
    private String email;

    @Column(nullable = false, unique = true) 
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) 
    private Role role;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false, updatable = false) 
    private LocalDateTime createdDate;

    @Column(nullable = false) 
    private String createdBy; 

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate; 

    private String updatedBy;

    private String deletedBy;

    private LocalDateTime deletedDate;

    private String suspendedBy;

    private LocalDateTime suspendedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private String verificationToken; // Verify account during creation

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verificationTokenExpiry;

    private String resetPasswordToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resetPasswordTokenExpiry;

}
