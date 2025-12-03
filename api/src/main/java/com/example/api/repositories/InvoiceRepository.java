package com.example.api.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.entities.Invoice;
import com.example.api.entities.enums.InvoiceStatus;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID>{
    List<Invoice> findByTenantUserIdOrderByDueDateDesc(UUID tenantId);
    
    @Query("SELECT i FROM Invoice i WHERE i.tenant.userId = :tenantId AND i.status IN ('PENDING', 'OVERDUE', 'PARTIAL')")
    List<Invoice> findPendingInvoicesForTenant(@Param("tenantId") UUID tenantId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :today AND i.status = 'PENDING'")
    List<Invoice> findOverdueInvoices(@Param("today") LocalDate today);

}
