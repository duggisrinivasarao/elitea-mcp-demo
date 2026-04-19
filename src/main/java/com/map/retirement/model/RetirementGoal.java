package com.map.retirement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * RetirementGoal Entity
 * Story: MAP-18 — Client Retirement Goal Definition
 * Epic:  MAP-5  — Retirement Planning Tools
 */
@Entity
@Table(name = "retirement_goals")
public class RetirementGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @NotNull
    @Min(value = 18)
    @Column(name = "current_age", nullable = false)
    private Integer currentAge;

    @NotNull
    @Min(value = 18)
    @Column(name = "target_retirement_age", nullable = false)
    private Integer targetRetirementAge;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "monthly_income_needed", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyIncomeNeeded;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "current_savings", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentSavings;

    @Column(name = "required_savings_target", precision = 15, scale = 2)
    private BigDecimal requiredSavingsTarget;

    @Column(name = "projected_monthly_savings_required", precision = 15, scale = 2)
    private BigDecimal projectedMonthlySavingsRequired;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
