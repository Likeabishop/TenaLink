package com.example.api.dtos;

import java.util.UUID;

import com.example.api.entities.enums.Role;
import com.example.api.entities.enums.UserStatus;

import lombok.Data;

@Data
public class UserDTO {
    private UUID userId;
    private String name;
    private String middleNames;
    private String surname;
    private String email;
    private Role role;
    private UserStatus status;
}
