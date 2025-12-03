package com.example.api.dtos.analytics;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OverviewStats {
    private Integer totalTenants;
    private Integer totalUnits;
    private Integer activeTenants;
    private BigDecimal monthlyRevenue;
    private BigDecimal ytdRevenue;
    private BigDecimal collectionRate;
    private Integer overDueInvoices;
}
