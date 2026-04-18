package com.edwardjones.onboarding.repository;

import com.edwardjones.onboarding.model.RiskTolerance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for RiskTolerance entity data access.
 * Story Reference: MAP-9 — Risk tolerance questionnaire
 */
@Repository
public interface RiskToleranceRepository extends JpaRepository<RiskTolerance, Long> {

    /**
     * Finds the risk tolerance assessment for a specific client.
     * @param clientId the client's ID
     * @return Optional containing the risk tolerance if found
     */
    Optional<RiskTolerance> findByClientId(Long clientId);

    /**
     * Checks if a risk tolerance assessment already exists for a client.
     * @param clientId the client's ID
     * @return true if exists
     */
    boolean existsByClientId(Long clientId);
}
