package com.map.retirement.repository;

import com.map.retirement.model.RetirementScenario;
import com.map.retirement.model.RetirementScenario.ScenarioType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for RetirementScenario persistence.
 * Story: MAP-19 — Retirement Scenario Simulation
 * Epic:  MAP-5  — Retirement Planning Tools
 */
@Repository
public interface RetirementScenarioRepository extends JpaRepository<RetirementScenario, Long> {

    /** Find all scenarios for a specific client */
    List<RetirementScenario> findByClientId(Long clientId);

    /** Find all scenarios for a given retirement goal */
    List<RetirementScenario> findByRetirementGoalId(Long retirementGoalId);

    /** Find a specific scenario type for a client's goal */
    Optional<RetirementScenario> findByClientIdAndRetirementGoalIdAndScenarioType(
            Long clientId, Long retirementGoalId, ScenarioType scenarioType);

    /** Delete all scenarios for a retirement goal */
    void deleteByRetirementGoalId(Long retirementGoalId);
}
