package com.example.api.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RevenueDTO {
    private BigDecimal monthlyRevenue;
    private Integer month;
    private Integer year;
    private BigDecimal ytdRevenue;
    private BigDecimal projectedRevenue;
    private BigDecimal revenueGrowth;
    private Double growthPercentage;
    private Map<String, BigDecimal> revenueByProperty;

    public RevenueDTO(BigDecimal monthlyRevenue, Integer month, Integer year) {
        this.monthlyRevenue = monthlyRevenue;
        this.month = month;
        this.year = year;
    }

    public RevenueDTO(
            BigDecimal monthlyRevenue,
            Integer month,
            Integer year,
            BigDecimal ytdRevenue,
            BigDecimal revenueGrowth,
            Double growthPercentage
        ) {
            this.monthlyRevenue = monthlyRevenue;
            this.month = month;
            this.year = year;
            this.ytdRevenue = ytdRevenue;
            this.revenueGrowth = revenueGrowth;
            this.growthPercentage = growthPercentage;
        }

    public String getPeriodName() {
        if (month != null && year != null) {
            LocalDate date = LocalDate.of(year, month, 1);
            return date.getMonth().toString() + " " + year;
        }
        return "Current Period";
    }
}
