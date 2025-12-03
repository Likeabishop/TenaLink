// package com.example.api.services;

// import java.math.BigDecimal;
// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;

// import com.example.api.dtos.payment.GatewayResponse;
// import com.example.api.entities.enums.PaymentMethod;
// import com.example.api.utils.PaymentGateway;
// import com.stripe.Stripe;
// import com.stripe.exception.StripeException;
// import com.stripe.model.Charge;
// import com.stripe.param.ChargeCreateParams;

// @Service
// public class StripePaymentGateway implements PaymentGateway {
    
//     private final String stripeSecretKey;

//     public StripePaymentGateway(@Value("${stripe.secret-key}") String stripeSecretKey) {
//         this.stripeSecretKey = stripeSecretKey;
//         Stripe.apiKey = this.stripeSecretKey; // Initialize Stripe once
//     }

//     @Override
//     public GatewayResponse processPayment(BigDecimal amount, PaymentMethod method, String paymentToken) {
//         try {
//             // Convert amount to cents (Stripe uses smallest currency unit)
//             long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

//             // Create charge parameters using the builder pattern (recommended approach)
//             ChargeCreateParams params = ChargeCreateParams.builder()
//                 .setAmount(amountInCents)
//                 .setCurrency("usd")
//                 .setSource(paymentToken)
//                 .setDescription("Rent payment")
//                 .build();

//             // Create the charge
//             Charge charge = Charge.create(params);

//             // Build and return success response
//             return GatewayResponse.builder()
//                 .success(charge.getPaid())
//                 .reference(charge.getId())
//                 .rawResponse(charge.toJson())
//                 .gatewayName("STRIPE")
//                 .build();
            
//         } catch (StripeException e) {
//             // Build and return error response
//             return GatewayResponse.builder()
//                 .success(false)
//                 .errorMessage(e.getMessage())
//                 .gatewayName("STRIPE")
//                 .build();
//         }
//     }

//     @Override
//     public GatewayResponse refundPayment(String paymentReference) {
//         try {
//             // Retrieve the charge first
//             Charge charge = Charge.retrieve(paymentReference);
            
//             // Create refund
//             com.stripe.model.Refund refund = com.stripe.model.Refund.create(
//                 Map.of("charge", paymentReference)
//             );

//             return GatewayResponse.builder()
//                 .success("succeeded".equals(refund.getStatus()))
//                 .reference(refund.getId())
//                 .rawResponse(refund.toJson())
//                 .gatewayName("STRIPE")
//                 .build();
            
//         } catch (StripeException e) {
//             return GatewayResponse.builder()
//                 .success(false)
//                 .errorMessage(e.getMessage())
//                 .gatewayName("STRIPE")
//                 .build();
//         }
//     }
// }