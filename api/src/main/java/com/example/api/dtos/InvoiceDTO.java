package com.example.api.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.example.api.entities.enums.InvoiceStatus;

import lombok.Data;

@Data
public class InvoiceDTO {
    private UUID invoiceId;
    private String invoiceNumber;
    private String description;
    private BigDecimal amount;
    private BigDecimal amountPaid;
    private BigDecimal balance;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private String propertyName;
    private String unitNumber;
}
