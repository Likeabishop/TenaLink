package com.example.api.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RevenueForecast {
    private String month;
    private BigDecimal projectedRevenue;
    private BigDecimal minRange;
    private BigDecimal maxRange;

    public RevenueForecast(String month, BigDecimal projectedRevenue) {
        this.month = month;
        this.projectedRevenue = projectedRevenue;
    }
}
