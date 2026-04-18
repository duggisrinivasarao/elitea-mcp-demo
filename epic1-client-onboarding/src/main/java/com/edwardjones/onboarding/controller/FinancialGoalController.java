package com.edwardjones.onboarding.controller;

import com.edwardjones.onboarding.dto.FinancialGoalRequest;
import com.edwardjones.onboarding.model.FinancialGoal;
import com.edwardjones.onboarding.service.FinancialGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for financial goal management.
 * Story Reference: MAP-12 — Set financial goals
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/clients/{clientId}/goals")
@RequiredArgsConstructor
public class FinancialGoalController {

    private final FinancialGoalService goalService;

    /**
     * Creates a new financial goal for a client.
     *
     * @param clientId the client's ID
     * @param request  the goal creation request
     * @return 201 Created with the new goal
     */
    @PostMapping
    public ResponseEntity<FinancialGoal> createGoal(
            @PathVariable Long clientId,
            @Valid @RequestBody FinancialGoalRequest request) {
        log.info("POST /api/v1/clients/{}/goals - type: {}", clientId, request.getGoalType());
        FinancialGoal goal = goalService.createGoal(clientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(goal);
    }

    /**
     * Retrieves all active financial goals for a client.
     *
     * @param clientId the client's ID
     * @return 200 OK with list of active goals
     */
    @GetMapping
    public ResponseEntity<List<FinancialGoal>> getGoals(@PathVariable Long clientId) {
        log.info("GET /api/v1/clients/{}/goals", clientId);
        List<FinancialGoal> goals = goalService.getActiveGoals(clientId);
        return ResponseEntity.ok(goals);
    }

    /**
     * Updates the current savings progress for a financial goal.
     *
     * @param clientId       the client's ID
     * @param goalId         the goal's ID
     * @param currentSavings the updated savings amount
     * @return 200 OK with updated goal
     */
    @PatchMapping("/{goalId}/progress")
    public ResponseEntity<FinancialGoal> updateGoalProgress(
            @PathVariable Long clientId,
            @PathVariable Long goalId,
            @RequestParam Double currentSavings) {
        log.info("PATCH /api/v1/clients/{}/goals/{}/progress", clientId, goalId);
        FinancialGoal updated = goalService.updateGoalProgress(goalId, currentSavings);
        return ResponseEntity.ok(updated);
    }
}
