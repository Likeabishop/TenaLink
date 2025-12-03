package com.example.api.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.api.entities.enums.UnitStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    @JoinColumn(name = "property_id", referencedColumnName = "propertyId")
    private Property property;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User tenant; // current tenant

    private BigDecimal monthlyRent;

    @Enumerated(EnumType.STRING)
    private UnitStatus status; 

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
     
}
