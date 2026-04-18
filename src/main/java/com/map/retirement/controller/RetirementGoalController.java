package com.map.retirement.controller;

import com.map.retirement.dto.RetirementGoalRequest;
import com.map.retirement.dto.RetirementGoalResponse;
import com.map.retirement.service.RetirementGoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for retirement goal management.
 * Story: MAP-18 — Client Retirement Goal & Savings Calculator
 * Epic:  MAP-5  — Retirement Planning Tools
 *
 * Endpoints:
 *   POST   /api/v1/clients/{clientId}/retirement/goals
 *   GET    /api/v1/clients/{clientId}/retirement/goals
 */
@RestController
@RequestMapping("/api/v1/clients/{clientId}/retirement/goals")
public class RetirementGoalController {

    private final RetirementGoalService retirementGoalService;

    public RetirementGoalController(RetirementGoalService retirementGoalService) {
        this.retirementGoalService = retirementGoalService;
    }

    /**
     * Creates or updates a client's retirement goal and returns savings calculation.
     *
     * @param clientId the client ID (path variable)
     * @param request  the retirement goal input
     * @return calculated retirement goal with required savings
     */
    @PostMapping
    public ResponseEntity<RetirementGoalResponse> saveRetirementGoal(
            @PathVariable Long clientId,
            @Valid @RequestBody RetirementGoalRequest request) {
        RetirementGoalResponse response = retirementGoalService.saveRetirementGoal(clientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all retirement goals for a client.
     *
     * @param clientId the client ID (path variable)
     * @return list of retirement goals
     */
    @GetMapping
    public ResponseEntity<List<RetirementGoalResponse>> getGoals(@PathVariable Long clientId) {
        return ResponseEntity.ok(retirementGoalService.getGoalsByClientId(clientId));
    }
}
