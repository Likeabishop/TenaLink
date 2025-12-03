package com.example.api.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.api.entities.enums.InvoiceStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Invoice {
    @Id
    @GeneratedValue
    private UUID invoiceId;

    private String invoiceNumber; // AUTO: INV-2024-001

    @ManyToOne
    @JoinColumn(name = "unit_id", referencedColumnName = "unitId")
    private Unit unit;

    @ManyToOne
    @JoinColumn(name = "tenant", referencedColumnName = "userId")
    private User tenant;

    private BigDecimal amount;
    private BigDecimal amountPaid = BigDecimal.ZERO;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private String description; // "Rent for October 2024"

    @OneToMany(mappedBy = "invoice")
    private List<Payment> payments = new ArrayList<>();

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