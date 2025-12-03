package com.example.api.dtos.payment;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentPatternDTO {
    private Map<Integer, BigDecimal> paymentsByDayOfMonth; // day -> amount
    private Map<String, BigDecimal> paymentsByDayOfWeek; // Monday -> amount
    private Integer peakPaymentDay;
    private String peakPaymentDayOfTheWeek;
    private BigDecimal averageDailyPayment;

    public PaymentPatternDTO(
            Map<Integer, BigDecimal> paymentsByDayOfMonth, 
            Map<String, BigDecimal> paymentsByDayOfWeek
    ) {
        this.paymentsByDayOfMonth = paymentsByDayOfMonth;
        this.paymentsByDayOfWeek = paymentsByDayOfWeek;
    }
}
