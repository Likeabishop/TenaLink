package com.example.api.dtos;

import java.util.UUID;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserPutDTO {
    private String name;
    private String middleNames;
}
