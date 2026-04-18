package com.map.retirement.dto;

import java.util.List;

/**
 * DTO for side-by-side comparison of all three retirement scenarios.
 * Story: MAP-19 — Multi-Scenario Comparison
 * Epic:  MAP-5  — Retirement Planning Tools
 */
public class ScenarioComparisonResponse {

    private Long               clientId;
    private Long               goalId;
    private List<ScenarioResponse> scenarios;

    // Getters and Setters
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getGoalId() { return goalId; }
    public void setGoalId(Long goalId) { this.goalId = goalId; }

    public List<ScenarioResponse> getScenarios() { return scenarios; }
    public void setScenarios(List<ScenarioResponse> scenarios) { this.scenarios = scenarios; }
}
