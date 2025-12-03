package com.example.api.utils;

import java.math.BigDecimal;

import com.example.api.dtos.payment.GatewayResponse;
import com.example.api.entities.enums.PaymentMethod;

public interface PaymentGateway {
    GatewayResponse processPayment(
        BigDecimal amount, 
        PaymentMethod method, 
        String paymentToken);
    GatewayResponse refundPayment(String paymentReference);
}
