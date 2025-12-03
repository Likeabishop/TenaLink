package com.example.api.dtos;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentStats {
    private BigDecimal totalCollected;
    private BigDecimal totalPending;
    private BigDecimal totalOverdue;
    private Integer paidInvoicesCount;
    private Integer pendingInvoicesCount;
    private Integer overdueInvoicesCount;
    private Map<String, BigDecimal> paymentMethods; // method -> amount

    public PaymentStats(
        BigDecimal totalCollected,
        BigDecimal totalPending,
        BigDecimal totalOverdue,
        Integer paidInvoicesCount,
        Integer pendingInvoicesCount,
        Integer overdueInvoicesCount
    ) {
        this.totalCollected = totalCollected;
        this.totalPending = totalPending;
        this.totalOverdue = totalOverdue;
        this.paidInvoicesCount = paidInvoicesCount;
        this.pendingInvoicesCount = pendingInvoicesCount;
        this.overdueInvoicesCount = overdueInvoicesCount;
    }
}
