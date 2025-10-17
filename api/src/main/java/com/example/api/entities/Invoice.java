package com.example.api.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.api.entities.enums.InvoiceStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
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
public class Invoice {
    @Id
    @GeneratedValue
    private UUID invoiceId;

    private String invoiceNumber; // AUTO: INV-2024-001

    @ManyToOne
    private Unit unit;

    @ManyToOne
    private User tenant;

    private BigDecimal amount;
    private BigDecimal amountPaid = BigDecimal.ZERO;

    private LocalDate dueDate;
    private LocalDate issueDate;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private String description; // "Rent for October 2024"

    @OneToMany(mappedBy = "invoice")
    private List<Payment> payments = new ArrayList<>();

}