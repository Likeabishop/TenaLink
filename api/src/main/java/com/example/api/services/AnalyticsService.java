package com.example.api.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.api.dtos.ComparisonDTO;
import com.example.api.dtos.OccupancyStats;
import com.example.api.dtos.PaymentStats;
import com.example.api.dtos.PaymentTimeline;
import com.example.api.dtos.PeriodStats;
import com.example.api.dtos.RevenueDTO;
import com.example.api.dtos.RevenueForecast;
import com.example.api.dtos.RevenueForecastDTO;
import com.example.api.dtos.analytics.AnalyticsDTO;
import com.example.api.dtos.analytics.MonthlyTrend;
import com.example.api.dtos.analytics.OverviewStats;
import com.example.api.dtos.payment.PaymentPatternDTO;
import com.example.api.entities.AnalyticsSnapshot;
import com.example.api.entities.Organization;
import com.example.api.repositories.AnalyticsCustomRepository;
import com.example.api.repositories.AnalyticsRepository;
import com.example.api.repositories.OrganizationRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AnalyticsService {
    
    private final AnalyticsRepository analyticsRepository;
    private final AnalyticsCustomRepository analyticsCustomRepository;
    private final OrganizationRepository organizationRepository;
    // private final PaymentRepository paymentRepository;

    public AnalyticsService(
        AnalyticsRepository analyticsRepository, 
        AnalyticsCustomRepository analyticsCustomRepository,
        OrganizationRepository organizationRepository
        /*PaymentRepository paymentRepository*/) {
            this.analyticsRepository = analyticsRepository;
            this.analyticsCustomRepository = analyticsCustomRepository;
            this.organizationRepository = organizationRepository;
    //         this.paymentRepository = paymentRepository;
    }
    

    @Scheduled(cron = "0 0 1 * * ?") // Run daily at midnight
    public void generateDailySnapshot() {
        List<Organization> organizations = organizationRepository.findAll();
        
        for (Organization org : organizations) {
            AnalyticsSnapshot snapshot = new AnalyticsSnapshot();
            snapshot.setOrganization(org);
            snapshot.setSnapshotDate(LocalDate.now());

            OverviewStats overview = analyticsCustomRepository.getOverviewStats(org.getOrganizationId());
            PaymentStats payments = analyticsCustomRepository.getPaymentStats(
                org.getOrganizationId(),
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now());
            OccupancyStats occupancy = analyticsCustomRepository.getOccupancyStats(
                org.getOrganizationId());
            
            // Populate snapshot
            snapshot.setTotalTenants(overview.getTotalTenants());
            snapshot.setActiveTenants(overview.getActiveTenants());
            snapshot.setTotalRevenue(payments.getTotalCollected()
                .add(payments.getTotalPending()));
            snapshot.setCollectedRevenue(payments.getTotalCollected());
            snapshot.setPendingRevenue(payments.getTotalPending());
            snapshot.setCollectionRate(overview.getCollectionRate());
            snapshot.setTotalUnits(occupancy.getTotalUnits());
            snapshot.setOccupiedUnits(occupancy.getOccupiedUnits());
            snapshot.setVacantUnits(occupancy.getVacantUnits());

            analyticsRepository.save(snapshot);
        }
    }

    public AnalyticsDTO getDashboardAnalytics(UUID organizationId) {
        AnalyticsDTO analytics = new AnalyticsDTO();

        analytics.setOverview(
            analyticsCustomRepository.getOverviewStats(organizationId));
        analytics.setPayments(
            analyticsCustomRepository.getPaymentStats(
                organizationId, 
                LocalDate.now().withDayOfMonth(1), 
                LocalDate.now()));
        analytics.setOccupancy(
            analyticsCustomRepository.getOccupancyStats(
                organizationId));
        analytics.setMonthlyTrends(
            analyticsCustomRepository.getMonthlyTrends(
                organizationId, 12));
        analytics.setPaymentTimeline(
            analyticsCustomRepository.getPaymentTimeline(
                organizationId, 
                LocalDate.now().minusDays(30), 
                LocalDate.now()));
        analytics.setUnitDistribution(
            analyticsCustomRepository
                .getUnitStatusDistribution(organizationId));
        
        return analytics;
    }

    public PaymentPatternDTO analyzePaymentPatterns(UUID organziationId) {
        // Analyze when payments typically come in
        List<PaymentTimeline> timeline = analyticsCustomRepository
            .getPaymentTimeline(
                organziationId, 
                LocalDate.now().minusYears(1), 
                LocalDate.now());
        
        Map<Integer, BigDecimal> paymentsByDayOfMonth = timeline.stream()
            .collect(Collectors.groupingBy(
                pt -> pt.getDate().getDayOfMonth(),
                Collectors.reducing(
                    BigDecimal.ZERO,
                    PaymentTimeline::getTotalAmount,
                    BigDecimal::add)
        ));

        Map<String, BigDecimal> paymentsByDayOfWeek = timeline.stream()
            .collect(Collectors.groupingBy(
                pt -> pt.getDate().getDayOfWeek().toString(),
                Collectors.reducing(
                    BigDecimal.ZERO,
                    PaymentTimeline::getTotalAmount,
                    BigDecimal::add)
        ));

        return new PaymentPatternDTO(
            paymentsByDayOfMonth, 
            paymentsByDayOfWeek
        );
    }

    public RevenueForecastDTO forecastRevenue(
        UUID organizationId, int months) {
            List<MonthlyTrend> trends = analyticsCustomRepository
                .getMonthlyTrends(organizationId, 12);
            
            // Simple forcasting based on historical trends
            BigDecimal averageMonthlyGrowth = calculateAverageGrowth(trends);
            BigDecimal lastMonthRevenue = trends.isEmpty() ? 
                BigDecimal.ZERO : trends.get(0).getRevenue();

            List<RevenueForecast> forecast = new ArrayList<>();
            for (int Index = 1; Index <= months; Index++) {
                BigDecimal projectedRevenue = lastMonthRevenue.multiply(
                    BigDecimal.ONE.add(
                        averageMonthlyGrowth.multiply(BigDecimal.valueOf(Index))));
                
                forecast.add(new RevenueForecast(
                    LocalDate.now().plusMonths(Index).getMonth().toString(),
                    projectedRevenue
                ));

            }

            return new RevenueForecastDTO(forecast, averageMonthlyGrowth);
    }

    public ComparisonDTO comparePeriods(
        UUID organizationId, String period1, String period2) {
        PeriodStats stats1 = getPeriodStats(organizationId, period1);
        PeriodStats stats2 = getPeriodStats(organizationId, period2);

        return new ComparisonDTO(stats1, stats2);
    }

    private PeriodStats getPeriodStats(UUID organizationId, String period) {
        YearMonth yearMonth = YearMonth.parse(period);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Get stats for this period
        OverviewStats overview = analyticsCustomRepository.getOverviewStatsForPeriod(
            organizationId, startDate, endDate);
        PaymentStats payments = analyticsCustomRepository.getPaymentStats(
            organizationId, startDate, endDate);
        OccupancyStats occupancy = analyticsCustomRepository.getOccupancyStats(
            organizationId);

        return new PeriodStats(
            yearMonth.getMonth().toString() + " " + yearMonth.getYear(),
            payments.getTotalCollected(),
            overview.getTotalTenants(),
            occupancy.getTotalUnits(),
            occupancy.getOccupiedUnits(),
            payments.getPaidInvoicesCount(),
            payments.getPaidInvoicesCount() + payments.getPendingInvoicesCount() +
            payments.getOverdueInvoicesCount(),
            overview.getCollectionRate(),
            occupancy.getOccupancyRate(),
            calculateAverageRent(organizationId)
        );

    }

    private BigDecimal calculateAverageRent(UUID organizationId) {
        // Calculating average rent across all units
        String query =
                "SELECT AVG(u.monthly_rent) FROM units u " +
                "JOIN properties p ON u.property_id = p.property_id " + 
                "WHERE p.organization_id = :orgId AND u.status = 'OCCUPIED'";
        // Execute query and return result
        return BigDecimal.valueOf(1500);
    }

    // public RevenueDTO getRevenueStats(UUID organizationId, int month, int year) {
    //     // Get monthly revenue from repository
    //     BigDecimal monthlyRevenue = paymentRepository.getMonthlyRevenue(organizationId, month, year);
        
    //     // Calculate year-to-date revenue (January to current month)
    //     BigDecimal ytdRevenue = BigDecimal.ZERO;
    //     for (int m = 1; m <= month; m++) {
    //         BigDecimal monthly = paymentRepository.getMonthlyRevenue(organizationId, m, year);
    //         ytdRevenue = ytdRevenue.add(monthly != null ? monthly : BigDecimal.ZERO);
    //     }
        
    //     // Calculate revenue growth compared to previous month
    //     BigDecimal previousMonthRevenue = BigDecimal.ZERO;
    //     Double growthPercentage = 0.0;
        
    //     if (month > 1) {
    //         previousMonthRevenue = paymentRepository.getMonthlyRevenue(organizationId, month - 1, year);
    //         if (previousMonthRevenue != null && previousMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
    //             BigDecimal growth = monthlyRevenue.subtract(previousMonthRevenue);
    //             growthPercentage = growth.divide(previousMonthRevenue, 4, java.math.RoundingMode.HALF_UP)
    //                 .multiply(BigDecimal.valueOf(100))
    //                 .doubleValue();
    //         }
    //     }
        
    //     // Return RevenueDTO using builder
    //     return new RevenueDTO(
    //         monthlyRevenue,
    //         month,
    //         year,
    //         ytdRevenue,
    //         monthlyRevenue.subtract(previousMonthRevenue),
    //         growthPercentage
    //     );
    // }


    private BigDecimal calculateAverageGrowth(List<MonthlyTrend> trends) {
        if (trends == null || trends.size() < 2) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalGrowth = BigDecimal.ZERO;
        int growthCount = 0;
        
        // Calculate growth between consecutive months
        for (int i = 1; i < trends.size(); i++) {
            BigDecimal currentRevenue = trends.get(i).getRevenue();
            BigDecimal previousRevenue = trends.get(i - 1).getRevenue();
            
            // Only calculate growth if both revenues are positive
            if (currentRevenue != null && previousRevenue != null &&
                previousRevenue.compareTo(BigDecimal.ZERO) > 0) {
                
                // Calculate growth percentage: ((current - previous) / previous)
                BigDecimal growth = currentRevenue.subtract(previousRevenue)
                    .divide(previousRevenue, 4, java.math.RoundingMode.HALF_UP);
                
                totalGrowth = totalGrowth.add(growth);
                growthCount++;
            }
        }
        
        // Return average growth, or zero if no valid growth calculations
        return growthCount > 0 ? 
            totalGrowth.divide(BigDecimal.valueOf(growthCount), 4, java.math.RoundingMode.HALF_UP) 
            : BigDecimal.ZERO;
    }
}
