package com.example.api.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.api.dtos.OccupancyStats;
import com.example.api.dtos.PaymentStats;
import com.example.api.dtos.PaymentTimeline;
import com.example.api.dtos.UnitStatusDistribution;
import com.example.api.dtos.analytics.MonthlyTrend;
import com.example.api.dtos.analytics.OverviewStats;
import com.example.api.entities.Payment;

import jakarta.persistence.EntityManager;

@Repository
public class AnalyticsCustomRepositoryImpl implements AnalyticsCustomRepository {
    private final EntityManager entityManager;

    public AnalyticsCustomRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public OverviewStats getOverviewStats(UUID organizationId) {
        String query = """
                SELECT 
                    COUNT(DISTINCT t.user_id) as totalTenants,
                    COUNT(DISTINCT CASE WHEN u.status = 'OCCUPIED' THEN t.user_id END)
                    as activeTenants,
                    COUNT(DISTINCT u.unit_id) as totalUnits,
                    COALESCE(SUM(CASE WHEN i.status = 'PAID' THEN i.amount ELSE
                    0 END), 0) as monthlyRevenue,
                    COALESCE(SUM(CASE WHEN i.status = 'PAID' AND YEAR(i.created_at)
                    = YEAR(CURRENT_DATE) THEN i.amount ELSE 0 END), 0) as ytdRevenue,
                    COUNT(CASE WHEN i.status = 'OVERDUE' THEN 1 END) as overdueInvoices
                    FROM units u
                    LEFT JOIN users t ON u.tenant_id = t.user_id AND t.role = 'USER'
                    LEFT JOIN invoices i ON u.unit_id = i.unit_id
                    WHERE u.property_id IN (
                        SELECT property_id FROM properties WHERE organization_id = : orgId
                    )
            """;

        try {
            // Execute the query
            Object[] result = (Object[]) entityManager.createNativeQuery(query)
                .setParameter("orgId", organizationId.toString())
                .getSingleResult();

            // Extract values from result array
            Integer totalTenants = ((Number) result[0]).intValue();
            Integer activeTenants = ((Number) result[1]).intValue();
            Integer totalUnits = ((Number) result[2]).intValue();
            BigDecimal monthlyRevenue = BigDecimal.valueOf(((Number) result[3]).doubleValue());
            BigDecimal ytdRevenue = BigDecimal.valueOf(((Number) result[4]).doubleValue());
            Integer overdueInvoices = ((Number) result[5]).intValue();
            BigDecimal collectionRate = BigDecimal.valueOf(((Number) result[6]).doubleValue());

            // Create and return OverviewStats object
            return new OverviewStats(
                totalTenants,
                totalUnits,
                activeTenants,
                monthlyRevenue,
                ytdRevenue,
                collectionRate,
                overdueInvoices
            );

        } catch (Exception e) {
            // Return default values in case of error
            return new OverviewStats(0, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0);
        }
    }

    @Override
    public PaymentStats getPaymentStats(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        String query = """
                SELECT 
                    COALESCE(SUM(CASE WHEN p.status = 'COMPLETED' THEN p.amount ELSE
                    0 END), 0) as totalCollected,
                    COALESCE(SUM(CASE WHEN i.status IN ('PENDING', 'PARTIAL') THEN
                    i.amount - i.amount_paid ELSE 0 END), 0) as totalPending,
                    COALESCE(SUM(CASE WHEN i.status = 'OVERDUE' THEN i.amount
                    - i.amount_paid ELSE 0 END), 0) as totalOverdue,
                    COUNT(CASE WHEN i.status = 'PAID' THEN 1 END) as paidInvoicesCount,
                    COUNT(CASE WHEN i.status IN ('PENDING', 'PARTIAL') THEN 1 END) as pendingInvoicesCount,
                    COUNT(CASE WHEN i.status = 'OVERDUE' THEN 1 END) as overdueInvoicesCount
                    FROM invoices i
                    JOIN units u ON i.unit_id = u.unit_id
                    LEFT JOIN payments pmt ON i.invoice_id = pmt.invoice_id
                    WHERE p.organization_id = :orgId
                    AND i.issue_date BETWEEN :startDate AND :endDate
            """;

        try {
            // Execute the query
            Object[] result = (Object[]) entityManager.createNativeQuery(query)
                .setParameter("orgId", organizationId.toString())
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();

            // Extract values from result array
            BigDecimal totalCollected = BigDecimal.valueOf(((Number) result[0]).doubleValue());
            BigDecimal totalPending = BigDecimal.valueOf(((Number) result[1]).doubleValue());
            BigDecimal totalOverdue = BigDecimal.valueOf(((Number) result[2]).doubleValue());
            Integer paidInvoicesCount = ((Number) result[3]).intValue();
            Integer pendingInvoicesCount = ((Number) result[4]).intValue();
            Integer overdueInvoicesCount = ((Number) result[5]).intValue();

            // Create and return PaymentStats object
            return new PaymentStats(
                totalCollected,
                totalPending,
                totalOverdue,
                paidInvoicesCount,
                pendingInvoicesCount,
                overdueInvoicesCount
            );

        } catch (Exception e) {
            // Return default values in case of error
            return new PaymentStats(
                BigDecimal.ZERO, 
                BigDecimal.ZERO, 
                BigDecimal.ZERO, 
                0, 
                0, 
                0
            );
        }
    }

    @Override
    public OccupancyStats getOccupancyStats(UUID organizationId) {
        String query = """
            SELECT 
                COUNT(u.unit_id) as totalUnits,
                COUNT(CASE WHEN u.status = 'OCCUPIED' THEN 1 END) as occupiedUnits,
                COUNT(CASE WHEN u.status = 'VACANT' THEN 1 END) as vacantUnits,
                COUNT(CASE WHEN u.status = 'MAINTENANCE' THEN 1 END) as maintenanceUnits
            FROM units u
            JOIN properties p ON u.property_id = p.property_id
            WHERE p.organization_id = :orgId
        """;

        try {
            // Execute the query
            Object[] result = (Object[]) entityManager.createNativeQuery(query)
                .setParameter("orgId", organizationId.toString())
                .getSingleResult();

            // Extract values from result array
            Integer totalUnits = ((Number) result[0]).intValue();
            Integer occupiedUnits = ((Number) result[1]).intValue();
            Integer vacantUnits = ((Number) result[2]).intValue();
            Integer maintenanceUnits = ((Number) result[3]).intValue();

            // Calculate rates
            Double occupancyRate = totalUnits > 0 ? (occupiedUnits * 100.0) / totalUnits : 0.0;
            Double vacancyRate = totalUnits > 0 ? (vacantUnits * 100.0) / totalUnits : 0.0;

            // Create and return OccupancyStats object
            return new OccupancyStats(
                totalUnits,
                occupiedUnits,
                vacantUnits,
                maintenanceUnits,
                occupancyRate,
                vacancyRate
            );

        } catch (Exception e) {
            // Return default values in case of error
            return new OccupancyStats(0, 0, 0, 0, 0.0, 0.0);
        }
    }

    @Override
    public List<MonthlyTrend> getMonthlyTrends(UUID organizationId, int months) {
        String query = """
            SELECT 
                DATE_FORMAT(p.paid_at, '%Y-%m') as month,
                COALESCE(SUM(p.amount), 0) as revenue,
                COUNT(p.payment_id) as paymentsCount,
                COUNT(DISTINCT CASE WHEN tenant.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH) THEN tenant.user_id END) as newTenants,
                CASE 
                    WHEN SUM(i.amount) > 0 THEN 
                        (COALESCE(SUM(CASE WHEN i.status = 'PAID' THEN i.amount ELSE 0 END), 0) / SUM(i.amount)) * 100 
                    ELSE 0 
                END as collectionRate
            FROM payments p
            JOIN invoices i ON p.invoice_id = i.invoice_id
            JOIN units u ON i.unit_id = u.unit_id
            JOIN properties prop ON u.property_id = prop.property_id
            LEFT JOIN users tenant ON u.tenant_id = tenant.user_id
            WHERE prop.organization_id = :orgId
            AND p.paid_at >= DATE_SUB(CURRENT_DATE, INTERVAL :months MONTH)
            AND p.status = 'COMPLETED'
            GROUP BY DATE_FORMAT(p.paid_at, '%Y-%m')
            ORDER BY month DESC
        """;

        try {
            // Execute the query
            List<Object[]> results = entityManager.createNativeQuery(query)
                .setParameter("orgId", organizationId.toString())
                .setParameter("months", months)
                .getResultList();

            // Map results to MonthlyTrend objects
            List<MonthlyTrend> trends = new ArrayList<>();
            for (Object[] result : results) {
                MonthlyTrend trend = new MonthlyTrend(
                    (String) result[0], // month
                    BigDecimal.valueOf(((Number) result[1]).doubleValue()), // revenue
                    ((Number) result[2]).intValue(), // paymentsCount
                    ((Number) result[3]).intValue(), // newTenants
                    ((Number) result[4]).doubleValue() // collectionRate
                );
                trends.add(trend);
            }

            return trends;

        } catch (Exception e) {
            e.printStackTrace();
            // Return empty list in case of error
            return new ArrayList<>();
        }
    }

    @Override
    public List<PaymentTimeline> getPaymentTimeline(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        String query = """
            SELECT 
                DATE(p.paid_at) as paymentDate, 
                COUNT(p.payment_id) as paymentCount,
                COALESCE(SUM(p.amount), 0) as totalAmount
            FROM payments p
            JOIN invoices i ON p.invoice_id = i.invoice_id
            JOIN units u ON i.unit_id = u.unit_id
            JOIN properties prop ON u.property_id = prop.property_id
            WHERE prop.organization_id = :orgId
            AND p.status = 'COMPLETED'
            AND p.paid_at BETWEEN :startDate AND :endDate
            GROUP BY DATE(p.paid_at)
            ORDER BY paymentDate
        """;

        try {
            // Execute the query
            List<Object[]> results = entityManager.createNativeQuery(query)
                .setParameter("orgId", organizationId.toString())
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();

            // Map results to PaymentTimeline objects
            List<PaymentTimeline> timeline = new ArrayList<>();
            for (Object[] result : results) {
                // Convert java.sql.Date to LocalDate
                java.sql.Date sqlDate = (java.sql.Date) result[0];
                LocalDate paymentDate = sqlDate.toLocalDate();
                
                PaymentTimeline entry = new PaymentTimeline(
                    paymentDate,
                    ((Number) result[1]).intValue(), // paymentCount
                    BigDecimal.valueOf(((Number) result[2]).doubleValue()) // totalAmount
                );
                timeline.add(entry);
            }

            return timeline;

        } catch (Exception e) {
            e.printStackTrace();
            // Return empty list in case of error
            return new ArrayList<>();
        }
    }

    @Override
    public List<UnitStatusDistribution> getUnitStatusDistribution(UUID organizationId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUnitStatusDistribution'");
    }

    @Override
    public OverviewStats getOverviewStatsForPeriod(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        String query = """
                SELECT 
                    COUNT(DISTINCT t.user_id) as totalTenants,
                    COUNT(DISTINCT CASE WHEN u.status = 'OCCUPIED' THEN t.user_id END)
                    as activeTenants,
                    COUNT(DISTINCT u.unit_id) as totalUnits, 
                    COALESCE(SUM(CASE WHEN i.status = 'PAID' AND i.issue_date BETWEEN
                    :startDate AND :endDate THEN i.amount ELSE 0 END), 0) as revenue,
                    COUNT(CASE WHEN i.status = 'OVERDUE' AND i.due_date BETWEEN
                    :startDate AND :endDate THEN 1 END) as overdueInvoices,
                    CASE 
                        WHEN SUM(i.amount) > 0 THEN 
                            (COALESCE(SUM(CASE WHEN i.status = 'PAID' THEN i.amount
                            ELSE 0 END), 0) / SUM(i.amount)) * 100 
                        ELSE 0
                    END as collectionRate
                FROM units u
                LEFT JOIN users t ON u.tenant_id = t.user_id AND t.role = 'USER'
                LEFT JOIN invoices i ON u.unit_id = i.unit_id
                WHERE u.property_id IN (
                    SELECT property_id FROM properties WHERE organization_id = :orgId
                )
            """;

        return new OverviewStats();
    }


}
