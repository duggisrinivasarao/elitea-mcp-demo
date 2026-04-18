package com.map.retirement.repository;

import com.map.retirement.model.RetirementGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for RetirementGoal persistence.
 * Story: MAP-18 — Client Retirement Goal Definition
 * Epic:  MAP-5  — Retirement Planning Tools
 */
@Repository
public interface RetirementGoalRepository extends JpaRepository<RetirementGoal, Long> {

    /** Find all retirement goals for a specific client */
    List<RetirementGoal> findByClientId(Long clientId);

    /** Find the latest retirement goal for a client */
    Optional<RetirementGoal> findTopByClientIdOrderByCreatedAtDesc(Long clientId);

    /** Check if a client already has a retirement goal */
    boolean existsByClientId(Long clientId);
}
