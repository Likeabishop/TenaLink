package com.example.api.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.entities.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByPaidByUserIdOrderByPaidAtDesc(UUID userId);

    List<Payment> findByInvoiceUnitPropertyOrganizationIdOrderByPaidAtDesc(UUID organizationId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.invoice.unit.property.organization.organizationId = :orgId AND p.status = 'COMPLETED' AND FUNCTION('MONTH', p.paidAt) = :month AND FUNCTION('YEAR', p.paidAt) = :year")
    BigDecimal getMonthlyRevenue(@Param("orgId") UUID orgId, @Param("month") int month, @Param("year") int year);
}
