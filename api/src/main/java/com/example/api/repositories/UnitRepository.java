package com.example.api.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.api.entities.Unit;
import com.example.api.entities.enums.UnitStatus;

public interface UnitRepository extends JpaRepository<Unit, UUID>{
    List<Unit> findByStatus(UnitStatus status);
}
