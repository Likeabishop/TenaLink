package com.example.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationRequest {
    private String firstname;
    private String lastname;
    private String middleNames;
    private String email;
    private String password;
    private String identificationNumber;
    private String createdBy;
}
