package com.map.retirement.dto;

import com.map.retirement.model.RetirementScenario.ScenarioType;
import java.math.BigDecimal;

/**
 * DTO for a single retirement scenario simulation result.
 * Story: MAP-19 — Scenario Simulation Output
 * Epic:  MAP-5  — Retirement Planning Tools
 */
public class ScenarioResponse {

    private Long         id;
    private Long         clientId;
    private ScenarioType scenarioType;
    private BigDecimal   monthlyContribution;
    private BigDecimal   annualReturnRate;
    private BigDecimal   projectedBalanceAtRetirement;
    private Boolean      isGoalAchievable;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public ScenarioType getScenarioType() { return scenarioType; }
    public void setScenarioType(ScenarioType scenarioType) { this.scenarioType = scenarioType; }

    public BigDecimal getMonthlyContribution() { return monthlyContribution; }
    public void setMonthlyContribution(BigDecimal monthlyContribution) { this.monthlyContribution = monthlyContribution; }

    public BigDecimal getAnnualReturnRate() { return annualReturnRate; }
    public void setAnnualReturnRate(BigDecimal annualReturnRate) { this.annualReturnRate = annualReturnRate; }

    public BigDecimal getProjectedBalanceAtRetirement() { return projectedBalanceAtRetirement; }
    public void setProjectedBalanceAtRetirement(BigDecimal projectedBalanceAtRetirement) { this.projectedBalanceAtRetirement = projectedBalanceAtRetirement; }

    public Boolean getIsGoalAchievable() { return isGoalAchievable; }
    public void setIsGoalAchievable(Boolean isGoalAchievable) { this.isGoalAchievable = isGoalAchievable; }
}
