package com.example.api.entities;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.api.entities.enums.UnitStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Unit {
    @Id
    @GeneratedValue
    private UUID unitId;

    private String unitNumber;

    @ManyToOne
    private Property property;

    @ManyToOne
    private User tenant; // current tenant

    private BigDecimal monthlyRent;

    @Enumerated(EnumType.STRING)
    private UnitStatus status; 
     
}
