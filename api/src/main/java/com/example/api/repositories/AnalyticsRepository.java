package com.example.api.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.api.entities.AnalyticsSnapshot;
import com.example.api.entities.Organization;

@Repository
public interface AnalyticsRepository extends JpaRepository<AnalyticsSnapshot, UUID> {

    Optional<AnalyticsSnapshot> findByOrganizationAndSnapshotDate(
        Organization organization, LocalDate date);
    
    List<AnalyticsSnapshot> findByOrganizationAndSnapshotDateBetween(
        Organization organization, LocalDate start, LocalDate end);
} 
