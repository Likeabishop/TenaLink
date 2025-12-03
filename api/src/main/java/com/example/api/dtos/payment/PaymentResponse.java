package com.example.api.dtos.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.api.entities.Payment;
import com.example.api.entities.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentResponse {
    private UUID paymentId;
    private String reference;
    private PaymentStatus status;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private String message;
    private GatewayResponse gatewayResponse;

    public PaymentResponse(
        Payment payment, 
        GatewayResponse gatewayResponse
    ) {
        this.gatewayResponse = gatewayResponse;
    }
}
