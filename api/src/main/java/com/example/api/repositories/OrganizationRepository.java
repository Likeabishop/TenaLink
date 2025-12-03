package com.example.api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.api.entities.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    
}
