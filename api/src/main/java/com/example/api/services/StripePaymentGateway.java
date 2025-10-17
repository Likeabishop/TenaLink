package com.example.api.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.api.entities.enums.PaymentMethod;
import com.example.api.utils.GatewayResponse;
import com.example.api.utils.PaymentGateway;

@Service
public class StripePaymentGateway implements PaymentGateway {
    
    private final String stripeSecretKey;

    public StripePaymentGateway(@Value("${stripe.secret-key}") String stripeSecretKey) {
        this.stripeSecretKey = stripeSecretKey;
    }

    @Override
    public GatewayResponse processPayment(BigDecimal amount, PaymentMethod method, String paymentToken) {
        Stripe.apiKey = stripeSecretKey;

        try {
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue()); // cents
            chargeParams.put("currency", "usd");
            chargeParams.put("source", paymentToken);
            chargeParams.put("description", "Rent payment");

            Charge charge = Charge.create(chargeParams);

            return GatewayResponse.builder()
                .success(charge.getPaid())
                .reference(charge.getId())
                .rawResponse(charge.toJson())
                .build();
            
        } catch (StripeException e) {
            return GatewayResponse.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }

    @Override
    public GatewayResponse refundPayment(String paymentReference) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refundPayment'");
    }
    
}
