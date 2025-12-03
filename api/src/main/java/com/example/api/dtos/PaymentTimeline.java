package com.example.api.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentTimeline {
    private LocalDate date;
    private Integer paymentCount;
    private BigDecimal totalAmount;
}
