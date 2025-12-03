package com.example.api.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PeriodStats {
    private String periodName; // "January 2024", "Q1 2024"
    private BigDecimal totalRevenue;
    private Integer totalTenants;
    private Integer totalUnits;
    private Integer occupiedUnits;
    private Integer paidInvoices;
    private Integer totalInvoices;
    private BigDecimal collectionRate;
    private Double occupancyRate;
    private BigDecimal averageRent;
}
