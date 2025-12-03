package com.example.api.dtos.analytics;

import java.util.List;

import com.example.api.dtos.OccupancyStats;
import com.example.api.dtos.PaymentStats;
import com.example.api.dtos.PaymentTimeline;
import com.example.api.dtos.UnitStatusDistribution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnalyticsDTO {
    private OverviewStats overview;
    private PaymentStats payments;
    private OccupancyStats occupancy;
    private List<MonthlyTrend> monthlyTrends;
    private List<PaymentTimeline> paymentTimeline;
    private List<UnitStatusDistribution> unitDistribution;
}
