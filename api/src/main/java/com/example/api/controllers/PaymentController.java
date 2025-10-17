package com.example.api.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.dtos.InvoiceDTO;
import com.example.api.dtos.PaymentRequest;
import com.example.api.dtos.PaymentResponse;
import com.example.api.entities.Invoice;
import com.example.api.entities.Payment;
import com.example.api.entities.User;
import com.example.api.repositories.InvoiceRepository;
import com.example.api.services.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final InvoiceRepository invoiceRepository;

    public PaymentController(
        PaymentService paymentService,
        InvoiceRepository invoiceRepository
    ) {
        this.paymentService = paymentService;
        this.invoiceRepository = invoiceRepository;
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> processPayment(
        @RequestBody @Valid PaymentRequest request,
        @AuthenticationPrincipal User user) {
            // Ensure user is paying their own invoice
            if (!request.getTenantId().equals(user.getUserId())) {
                throw new RuntimeException("Cannot pay invoice for another user");
            }

            PaymentResponse response = paymentService.processPayment(request);
            return ResponseEntity.ok(response);
    }

    @GetMapping("/invoices/my-invoices")
    public List<InvoiceDTO> getMyInvoices(@AuthenticationPrincipal User user) {
        List<Invoice> invoices = invoiceRepository
            .findByTenantUserIdOrderByDueDateDesc(
                user.getUserId());

        return invoices.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @GetMapping("/payment-history")
    public List<Payment> getPaymentHistory(@AuthenticationPrincipal User user) {
        return paymentService.retrievePaymentHistory(user);
    }

    // Admin endpoints
    @GetMapping("/admin?revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public RevenueDTO getRevenueStats(
        @AuthenticationPrincipal User admin,
        @RequestParam(required = false) Integer month,
        @RequestParam(required = false) Integer year) {
            LocalDate now = LocalDate.now();
            int targetMonth = month != null ? month : now.getMonthValue(
                admin.getOrganization().getOrganizationId(), targetMonth, targetYear);
            
            return new RevenueDTO(monthlyRevenue, targetMonth, targetYear);
        }
        
}
