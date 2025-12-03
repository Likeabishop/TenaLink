package com.example.api.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.api.dtos.OccupancyStats;
import com.example.api.dtos.PaymentStats;
import com.example.api.dtos.PaymentTimeline;
import com.example.api.dtos.UnitStatusDistribution;
import com.example.api.dtos.analytics.MonthlyTrend;
import com.example.api.dtos.analytics.OverviewStats;

// Custom repository for complex analytics queries
@Repository
public interface AnalyticsCustomRepository {
    
    OverviewStats getOverviewStats(UUID organizationId);
    OverviewStats getOverviewStatsForPeriod(
        UUID organizationId, 
        LocalDate startDate,
        LocalDate endDate);
    PaymentStats getPaymentStats(
        UUID organizationId, 
        LocalDate startDate, 
        LocalDate endDate);
    OccupancyStats getOccupancyStats(UUID organizationId);
    List<MonthlyTrend> getMonthlyTrends(
        UUID organizationId, int months);
    List<PaymentTimeline> getPaymentTimeline(
        UUID organizationId, 
        LocalDate startDate, 
        LocalDate endDate);
    List<UnitStatusDistribution> getUnitStatusDistribution(
        UUID organizationId);
}
