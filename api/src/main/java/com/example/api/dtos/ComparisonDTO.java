package com.example.api.dtos;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ComparisonDTO {
    private PeriodStats currentPeriod;
    private PeriodStats previousPeriod;
    private BigDecimal revenueGrowth;
    private Double occupancyChange;
    private Double collectionRateChange;
    private Integer tenantGrowth;
    private BigDecimal revenueGrowthPercentage;

    public ComparisonDTO(PeriodStats currentPeriod, PeriodStats previousPeriod) {
        this.currentPeriod = currentPeriod;
        this.previousPeriod = previousPeriod;
        calculateChanges();
    }

    private void calculateChanges() {
        // Revenue Growth
        if (previousPeriod.getTotalRevenue() != null && 
            previousPeriod.getTotalRevenue()
                .compareTo(BigDecimal.ZERO) > 0) {
            this.revenueGrowth = currentPeriod.getTotalRevenue()
                .subtract(previousPeriod.getTotalRevenue());
            this.revenueGrowthPercentage = revenueGrowth.divide(
                previousPeriod.getTotalRevenue(), 
                4, 
                RoundingMode.HALF_UP).multiply(
                    BigDecimal.valueOf(100));
        }

        // Occupancy Change
        if (previousPeriod.getOccupancyRate() != null) {
            this.occupancyChange = currentPeriod.getOccupancyRate() -
            previousPeriod.getOccupancyRate();
        }

        // Collection Rate Change
        if (previousPeriod.getCollectionRate() != null) {
            this.collectionRateChange = currentPeriod.getCollectionRate().doubleValue() -
            previousPeriod.getCollectionRate().doubleValue();
        }

        // Tenant Change
        if (previousPeriod.getTotalTenants() != null) {
            this.tenantGrowth = currentPeriod.getTotalTenants() -
            previousPeriod.getTotalTenants();
        }
    }
}
