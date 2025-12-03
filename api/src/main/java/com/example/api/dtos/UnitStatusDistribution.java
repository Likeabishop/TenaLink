package com.example.api.dtos;

import lombok.Data;

@Data
public class UnitStatusDistribution {
    private String status;
    private Long count;
    private Double percentage;
}
