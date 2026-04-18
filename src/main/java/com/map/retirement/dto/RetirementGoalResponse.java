package com.map.retirement.dto;

import java.math.BigDecimal;

/**
 * DTO for retirement goal calculation response.
 * Story: MAP-18 — Retirement Goal Output
 * Epic:  MAP-5  — Retirement Planning Tools
 */
public class RetirementGoalResponse {

    private Long       id;
    private Long       clientId;
    private Integer    currentAge;
    private Integer    targetRetirementAge;
    private BigDecimal monthlyIncomeNeeded;
    private BigDecimal currentSavings;
    private BigDecimal requiredSavingsTarget;
    private BigDecimal projectedMonthlySavingsRequired;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Integer getCurrentAge() { return currentAge; }
    public void setCurrentAge(Integer currentAge) { this.currentAge = currentAge; }

    public Integer getTargetRetirementAge() { return targetRetirementAge; }
    public void setTargetRetirementAge(Integer targetRetirementAge) { this.targetRetirementAge = targetRetirementAge; }

    public BigDecimal getMonthlyIncomeNeeded() { return monthlyIncomeNeeded; }
    public void setMonthlyIncomeNeeded(BigDecimal monthlyIncomeNeeded) { this.monthlyIncomeNeeded = monthlyIncomeNeeded; }

    public BigDecimal getCurrentSavings() { return currentSavings; }
    public void setCurrentSavings(BigDecimal currentSavings) { this.currentSavings = currentSavings; }

    public BigDecimal getRequiredSavingsTarget() { return requiredSavingsTarget; }
    public void setRequiredSavingsTarget(BigDecimal requiredSavingsTarget) { this.requiredSavingsTarget = requiredSavingsTarget; }

    public BigDecimal getProjectedMonthlySavingsRequired() { return projectedMonthlySavingsRequired; }
    public void setProjectedMonthlySavingsRequired(BigDecimal projectedMonthlySavingsRequired) { this.projectedMonthlySavingsRequired = projectedMonthlySavingsRequired; }
}
