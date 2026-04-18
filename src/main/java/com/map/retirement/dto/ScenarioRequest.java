package com.map.retirement.dto;

import com.map.retirement.model.RetirementScenario.ScenarioType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for retirement scenario simulation request.
 * Story: MAP-19 — Scenario Simulation Input
 * Epic:  MAP-5  — Retirement Planning Tools
 */
public class ScenarioRequest {

    @NotNull(message = "Scenario type is required")
    private ScenarioType scenarioType;

    @NotNull(message = "Monthly contribution is required")
    @DecimalMin(value = "0.01", message = "Monthly contribution must be greater than zero")
    private BigDecimal monthlyContribution;

    // Getters and Setters
    public ScenarioType getScenarioType() { return scenarioType; }
    public void setScenarioType(ScenarioType scenarioType) { this.scenarioType = scenarioType; }

    public BigDecimal getMonthlyContribution() { return monthlyContribution; }
    public void setMonthlyContribution(BigDecimal monthlyContribution) { this.monthlyContribution = monthlyContribution; }
}
