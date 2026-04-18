package com.map.retirement.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for creating/updating a retirement goal.
 * Story: MAP-18 — Retirement Goal Input
 * Epic:  MAP-5  — Retirement Planning Tools
 */
public class RetirementGoalRequest {

    @NotNull(message = "Current age is required")
    @Min(value = 18, message = "Current age must be at least 18")
    @Max(value = 100, message = "Current age must be realistic")
    private Integer currentAge;

    @NotNull(message = "Target retirement age is required")
    @Min(value = 18, message = "Target retirement age must be at least 18")
    @Max(value = 100, message = "Target retirement age must be realistic")
    private Integer targetRetirementAge;

    @NotNull(message = "Monthly income needed is required")
    @DecimalMin(value = "0.01", message = "Monthly income needed must be greater than zero")
    private BigDecimal monthlyIncomeNeeded;

    @NotNull(message = "Current savings amount is required")
    @DecimalMin(value = "0.0", message = "Current savings cannot be negative")
    private BigDecimal currentSavings;

    // Getters and Setters
    public Integer getCurrentAge() { return currentAge; }
    public void setCurrentAge(Integer currentAge) { this.currentAge = currentAge; }

    public Integer getTargetRetirementAge() { return targetRetirementAge; }
    public void setTargetRetirementAge(Integer targetRetirementAge) { this.targetRetirementAge = targetRetirementAge; }

    public BigDecimal getMonthlyIncomeNeeded() { return monthlyIncomeNeeded; }
    public void setMonthlyIncomeNeeded(BigDecimal monthlyIncomeNeeded) { this.monthlyIncomeNeeded = monthlyIncomeNeeded; }

    public BigDecimal getCurrentSavings() { return currentSavings; }
    public void setCurrentSavings(BigDecimal currentSavings) { this.currentSavings = currentSavings; }
}
