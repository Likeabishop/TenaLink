package com.example.api.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name= "analytics_snapshot")
public class AnalyticsSnapshot {
    @Id
    @GeneratedValue
    private UUID snapshotId;
    
    @ManyToOne
    @JoinColumn(name = "organization_id", referencedColumnName = "organizationId")
    private Organization organization;

    private LocalDate snapshotDate;

    // Tenant Statistics
    private Integer totalTenants;
    private Integer activeTenants;
    private Integer newTenantsThisMonth;

    // Payment Statistics
    private Integer totalInvoices;
    private Integer paidInvoices;
    private Integer overdueInvoices;
    private Integer pendingInvoices;

    private BigDecimal totalRevenue;
    private BigDecimal collectedRevenue;
    private BigDecimal pendingRevenue;

    // Property Statistics
    private Integer totalUnits;
    private Integer occupiedUnits;
    private Integer vacantUnits;
    private Integer maintenanceUnits;

    // Collection Rate
    private BigDecimal collectionRate; // (collectedRevenue / totalRevenue) * 100

}
