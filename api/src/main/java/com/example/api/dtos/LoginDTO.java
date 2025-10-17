package com.example.api.dtos;

import lombok.Data;

@Data
public class LoginDTO {
    private String password;
    private String email;
}
