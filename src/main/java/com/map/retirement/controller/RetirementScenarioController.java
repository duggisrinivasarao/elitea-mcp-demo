package com.map.retirement.controller;

import com.map.retirement.dto.ScenarioComparisonResponse;
import com.map.retirement.dto.ScenarioRequest;
import com.map.retirement.dto.ScenarioResponse;
import com.map.retirement.service.RetirementScenarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST Controller for retirement scenario simulations.
 * Story: MAP-19 — Retirement Scenario Simulation
 * Epic:  MAP-5  — Retirement Planning Tools
 *
 * Endpoints:
 *   POST /api/v1/clients/{clientId}/retirement/goals/{goalId}/scenarios
 *   GET  /api/v1/clients/{clientId}/retirement/goals/{goalId}/scenarios/compare?monthlyContribution=X
 */
@RestController
@RequestMapping("/api/v1/clients/{clientId}/retirement/goals/{goalId}/scenarios")
public class RetirementScenarioController {

    private final RetirementScenarioService scenarioService;

    public RetirementScenarioController(RetirementScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    /**
     * Runs a single retirement scenario simulation.
     *
     * @param clientId the client ID
     * @param goalId   the retirement goal ID
     * @param request  scenario type and monthly contribution
     * @return projected scenario result
     */
    @PostMapping
    public ResponseEntity<ScenarioResponse> runScenario(
            @PathVariable Long clientId,
            @PathVariable Long goalId,
            @Valid @RequestBody ScenarioRequest request) {
        return ResponseEntity.ok(scenarioService.runScenario(clientId, goalId, request));
    }

    /**
     * Compares all three scenario types (conservative, moderate, aggressive) side by side.
     *
     * @param clientId            the client ID
     * @param goalId              the retirement goal ID
     * @param monthlyContribution monthly contribution to compare across all scenarios
     * @return side-by-side comparison of all three projections
     */
    @GetMapping("/compare")
    public ResponseEntity<ScenarioComparisonResponse> compareScenarios(
            @PathVariable Long clientId,
            @PathVariable Long goalId,
            @RequestParam BigDecimal monthlyContribution) {
        return ResponseEntity.ok(scenarioService.compareAllScenarios(clientId, goalId, monthlyContribution));
    }
}
