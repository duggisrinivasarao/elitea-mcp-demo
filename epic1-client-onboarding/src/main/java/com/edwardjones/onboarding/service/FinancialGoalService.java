package com.edwardjones.onboarding.service;

import com.edwardjones.onboarding.dto.FinancialGoalRequest;
import com.edwardjones.onboarding.model.Client;
import com.edwardjones.onboarding.model.FinancialGoal;
import com.edwardjones.onboarding.repository.ClientRepository;
import com.edwardjones.onboarding.repository.FinancialGoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing client financial goals.
 * Story Reference: MAP-12 — Set financial goals
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialGoalService {

    private final FinancialGoalRepository goalRepository;
    private final ClientRepository clientRepository;

    /**
     * Creates a new financial goal for a client.
     *
     * @param clientId the client's ID
     * @param request  the goal creation request
     * @return the saved FinancialGoal entity
     * @throws jakarta.persistence.EntityNotFoundException if client not found
     * @throws IllegalArgumentException if target amount is invalid
     */
    @Transactional
    public FinancialGoal createGoal(Long clientId, FinancialGoalRequest request) {
        log.info("Creating {} goal for client ID: {}", request.getGoalType(), clientId);

        if (request.getTargetAmount() <= 0) {
            throw new IllegalArgumentException("Target amount must be greater than zero.");
        }

        if (request.getTargetDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Target date must be in the future.");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Client not found with ID: " + clientId
                ));

        FinancialGoal goal = FinancialGoal.builder()
                .client(client)
                .goalName(request.getGoalName())
                .goalType(request.getGoalType())
                .targetAmount(request.getTargetAmount())
                .currentSavings(request.getCurrentSavings() != null ? request.getCurrentSavings() : 0.0)
                .targetDate(request.getTargetDate())
                .priority(request.getPriority())
                .notes(request.getNotes())
                .status(FinancialGoal.GoalStatus.ACTIVE)
                .build();

        FinancialGoal saved = goalRepository.save(goal);
        log.info("Financial goal created with ID: {} for client: {}", saved.getId(), clientId);

        return saved;
    }

    /**
     * Retrieves all active financial goals for a client.
     *
     * @param clientId the client's ID
     * @return list of active financial goals
     */
    @Transactional(readOnly = true)
    public List<FinancialGoal> getActiveGoals(Long clientId) {
        return goalRepository.findByClientIdAndStatus(clientId, FinancialGoal.GoalStatus.ACTIVE);
    }

    /**
     * Updates the current savings progress for a financial goal.
     *
     * @param goalId         the goal's ID
     * @param currentSavings the updated savings amount
     * @return the updated FinancialGoal
     * @throws jakarta.persistence.EntityNotFoundException if goal not found
     */
    @Transactional
    public FinancialGoal updateGoalProgress(Long goalId, Double currentSavings) {
        FinancialGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Financial goal not found with ID: " + goalId
                ));

        goal.setCurrentSavings(currentSavings);

        if (currentSavings >= goal.getTargetAmount()) {
            goal.setStatus(FinancialGoal.GoalStatus.ACHIEVED);
            log.info("Goal {} has been achieved!", goalId);
        }

        return goalRepository.save(goal);
    }
}
