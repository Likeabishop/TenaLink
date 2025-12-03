package com.example.api.dtos.analytics;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MonthlyTrend {
    private String month; // "Jan 2024"
    private BigDecimal revenue;
    private Integer paymentsCount;
    private Integer newTenants;
    private Double collectionRate;
}
