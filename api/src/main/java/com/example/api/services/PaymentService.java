package com.example.api.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.api.dtos.PaymentRequest;
import com.example.api.dtos.PaymentResponse;
import com.example.api.entities.Invoice;
import com.example.api.entities.Payment;
import com.example.api.entities.Unit;
import com.example.api.entities.User;
import com.example.api.entities.enums.InvoiceStatus;
import com.example.api.entities.enums.PaymentStatus;
import com.example.api.entities.enums.UnitStatus;
import com.example.api.exceptions.ResourceNotFoundException;
import com.example.api.repositories.InvoiceRepository;
import com.example.api.repositories.PaymentRepository;
import com.example.api.repositories.UnitRepository;
import com.example.api.repositories.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PaymentService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final EmailService emailService;
    private final PaymentGateway paymentGateway;
    private final String clientUrl;

    public PaymentService(
        InvoiceRepository invoiceRepository,
        PaymentRepository paymentRepository,
        UserRepository userRepository,
        UnitRepository unitRepository,
        EmailService emailService,
        PaymentGateway paymentGateway,
        @Value("${app.client-url}") String clientUrl) {
            this.invoiceRepository = invoiceRepository;
            this.paymentRepository = paymentRepository;
            this.userRepository = userRepository;
            this.unitRepository = unitRepository;
            this.emailService = emailService;
            this.paymentGateway = paymentGateway;
            this.clientUrl = clientUrl;
    }

    // Initialize payment monthly
    @Scheduled(cron = "0 0 1 1 * ?") // 1st day of every month
    public void generateMonthlyInvoices() {
        LocalDate today = LocalDate.now();

        // Find all occupied units
        List<Unit> occupiedUnits = unitRepository.findByStatus(UnitStatus.OCCUPIED);

        for (Unit unit : occupiedUnits) {
            Invoice invoice = new Invoice();
            invoice.setUnit(unit);
            invoice.setTenant(unit.getTenant());
            invoice.setAmount(unit.getMonthlyRent());
            invoice.setIssueDate(today);
            invoice.setDueDate(today.withDayOfMonth(unit.getProperty().getDueDay()));
            invoice.setStatus(InvoiceStatus.PENDING);
            invoice.setDescription("Rent For " + today.getMonth().toString() + " " + today.getYear());
            invoice.setInvoiceNumber(generateInvoiceNumber());

            invoiceRepository.save(invoice);

            // Notify tenant
            sendInvoiceNotification(invoice);
        } 
    }

    // Process Payment
    public PaymentResponse processPayment(PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        User tenant = userRepository.findById(request.getTenantId())
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        
        // Validate tenant owns the invoice
        if (!invoice.getTenant().getUserId().equals(tenant.getUserId())) {
            throw new RuntimeException("Invoice does not belong to this tenant");
        }

        // Process payment with gateway
        GatewayResponse gatewayResponse = paymentGateway.processPayment(
            request.getAmount(),
            request.getPaymentMethod(),
            request.getPaymentToken() // From frontend (Stripe, etc)
        );

        // Create payment record
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setPaidBy(tenant);
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getPaymentMethod());
        payment.setPaidAt(LocalDateTime.now());
        payment.setPaymentReference(gatewayResponse.getReference());
        payment.setGateWayResponse(gatewayResponse.getRawResponse());
        payment.setStatus(gatewayResponse.isSuccess() ?
            PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        
        Payment savedPayment = paymentRepository.save(payment);

        // Update inovice status
        if (gatewayResponse.isSuccess()) {
            updateInvoiceStatus(invoice, request.getAmount());
            sendPaymentConfirmation(savedPayment);
        }

        return new PaymentResponse(savedPayment, gatewayResponse);
    }

    public List<Payment> retrievePaymentHistory(User user) {
        return paymentRepository.findByPaidByUserIdOrderByPaidAtDesc(user.getUserId())
    }

    private void updateInvoiceStatus(
        Invoice invoice, 
        BigDecimal amountPaid) {
            BigDecimal totalPaid = invoice.getPayments().stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(amountPaid);
            
            invoice.setAmountPaid(amountPaid);

            if (totalPaid.compareTo(invoice.getAmount()) >= 0) {
                invoice.setStatus(InvoiceStatus.PAID);
            } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                invoice.setStatus(InvoiceStatus.PARTIAL);
            } 

            invoiceRepository.save(invoice);
    }

    private void sendInvoiceNotification(Invoice invoice) throws MessagingException {
        Map<String, Object> variables = Map.of(
            "tenantName", invoice.getTenant().getName(),
            "amount", invoice.getAmount(),
            "dueDate", invoice.getDueDate(),
            "invoiceNumber", invoice.getInvoiceNumber(),
            "paymentLink", clientUrl + "/pay-invoice/" + invoice.getInvoiceId()
        );

        emailService.sendEmail(
            invoice.getTenant().getEmail(), 
            "Rent Invoice - " + invoice.getDescription(), 
            "rent_invoice", 
            variables);
    }

    private void sendPaymentConfirmation(Payment payment) throws MessagingException {
        Map<String, Object> variables = Map.of(
            "tenantName", payment.getPaidBy().getName(),
            "amount", payment.getAmount(),
            "invoiceNumber", payment.getInvoice().getInvoiceNumber(),
            "paymentDate", payment.getPaidAt(),
            "reference", payment.getPaymentReference()
        );

        emailService.sendEmail(
            payment.getPaidBy().getEmail(), 
            "Payment Confirmation", 
            "payment_confirmation", 
            variables);
        
        // Also notify admin
        notifyAdminOfPayment();
    }

    private String generateInvoiceNumber() {
        String prefix = "INV";
        String year = String.valueOf(LocalDate.now().getYear());
        Long sequence = invoiceRepository.count() + 1;
        return String.format(
            "%s-%s-%03d", 
            prefix,
            year,
            sequence);
    }
}
