package com.example.api.dtos.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GatewayResponse {
    private boolean success;
    private String reference;
    private String rawResponse;
    private String errorMessage;
    private String gatewayName; // "STRIPE", "PAYPAL", etc

    public GatewayResponse(
        boolean success,
        String reference,
        String rawResponse
    ) {
        this.success = success;
        this.reference = reference;
        this.rawResponse = rawResponse;
    }

    public GatewayResponse(
        boolean success,
        String errorMessage
    ) {
        this.success = success;
        this.errorMessage = errorMessage;
    }
}
