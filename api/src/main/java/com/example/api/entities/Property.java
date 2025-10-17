package com.example.api.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID propertyId;

    private String name;
    private String address;

    @ManyToOne
    private Organization organization;

    private BigDecimal monthlyRent;
    private Integer dueDay; // 1st of every month

    @OneToMany(mappedBy = "propery")
    private List<Unit> units;

    private LocalDateTime createdAt;
}