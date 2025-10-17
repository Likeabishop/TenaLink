package com.example.api.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.api.entities.enums.PaymentStatus;

import lombok.Data;

@Data
public class PaymentResponse {
    private UUID paymentId;
    private String reference;
    private PaymentStatus status;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private String message;
}
