package com.example.api.dtos;

import java.util.UUID;

import com.example.api.entities.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class UserAdminUpdateDTO {
    private String name;
    private String middleNames;
    private String surname;
    private String identificationNumber;
    private String email;
    private String password;
    private Role role;
}
