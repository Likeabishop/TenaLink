package com.example.api.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RevenueForecastDTO {
    private List<RevenueForecast> forecast;
    private BigDecimal estimatedGrowthRate;
    private String confidenceLevel; // HIGH, MEDIUM, LOW

    public RevenueForecastDTO(
        List<RevenueForecast> forecast, 
        BigDecimal estimatedGrowthRate
    ) {
        this.forecast = forecast;
        this.estimatedGrowthRate = estimatedGrowthRate;
        this.confidenceLevel = calculateConfidenceLevel(estimatedGrowthRate);
    }

    private String calculateConfidenceLevel(BigDecimal growthRate) {
        if (growthRate == null) {
            return "LOW";
        }
        
        BigDecimal absoluteRate = growthRate.abs();
        if (absoluteRate.compareTo(BigDecimal.valueOf(0.05)) < 0) { // < 5%
            return "HIGH";
        } else if (absoluteRate.compareTo(BigDecimal.valueOf(0.15)) < 0) { // < 15%
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
}
