package com.example.api.controllers;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.dtos.ComparisonDTO;
import com.example.api.dtos.RevenueForecastDTO;
import com.example.api.dtos.analytics.AnalyticsDTO;
import com.example.api.entities.User;
import com.example.api.entities.enums.Role;
import com.example.api.services.AnalyticsService;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public AnalyticsDTO getDashboardAnalytics(@AuthenticationPrincipal User user) {
        UUID organizationId = user.getRole() == Role.SUPER_ADMIN ?
        null : user.getOrganization().getOrganizationId();

        return analyticsService.getDashboardAnalytics(organizationId);
    }

    @GetMapping("/payment-patterns")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public RevenueForecastDTO getRevenueForecast( // Also fixed the method name here
        @AuthenticationPrincipal User user,
        @RequestParam(defaultValue = "6") int months) {
            UUID organizationId = user.getRole() == Role.SUPER_ADMIN ?
            null : user.getOrganization().getOrganizationId();

            return analyticsService.forecastRevenue( // Fixed: forcastRevenue -> forecastRevenue
                organizationId, months);
    }

    @GetMapping("/comparison")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ComparisonDTO getPeriodComparison(
        @AuthenticationPrincipal User user,
        @RequestParam String period1, // "2024-01"
        @RequestParam String period2) {
        
            // Compare two periods (month over month, year over year)
            return analyticsService.comparePeriods(
                user.getOrganization().getOrganizationId(), period1, period2);
    }
}
