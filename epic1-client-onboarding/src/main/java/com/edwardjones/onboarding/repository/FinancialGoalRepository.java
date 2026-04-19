package com.edwardjones.onboarding.repository;

import com.edwardjones.onboarding.model.FinancialGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for FinancialGoal entity data access.
 * Story Reference: MAP-12 — Set financial goals
 */
@Repository
public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {

    /**
     * Finds all financial goals for a specific client.
     * @param clientId the client's ID
     * @return list of financial goals
     */
    List<FinancialGoal> findByClientId(Long clientId);

    /**
     * Finds all active financial goals for a specific client.
     * @param clientId the client's ID
     * @param status goal status
     * @return list of financial goals
     */
    List<FinancialGoal> findByClientIdAndStatus(Long clientId, FinancialGoal.GoalStatus status);

    /**
     * Finds goals by type for a specific client.
     * @param clientId the client's ID
     * @param goalType the type of goal
     * @return list of matching goals
     */
    List<FinancialGoal> findByClientIdAndGoalType(Long clientId, FinancialGoal.GoalType goalType);
}
