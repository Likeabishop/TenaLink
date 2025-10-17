package com.example.api.dtos;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.api.entities.enums.PaymentMethod;

import lombok.Data;

@Data
public class PaymentRequest {
    private UUID invoiceId;
    private UUID tenantId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String paymentToken; // From Stripe, etc
}
