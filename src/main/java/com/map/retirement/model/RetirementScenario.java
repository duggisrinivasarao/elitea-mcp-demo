package com.map.retirement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * RetirementScenario Entity
 * Story: MAP-19 — Retirement Scenario Simulation
 * Epic:  MAP-5  — Retirement Planning Tools
 */
@Entity
@Table(name = "retirement_scenarios")
public class RetirementScenario {

    public enum ScenarioType {
        CONSERVATIVE, MODERATE, AGGRESSIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retirement_goal_id", nullable = false)
    private RetirementGoal retirementGoal;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "scenario_type", nullable = false)
    private ScenarioType scenarioType;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "monthly_contribution", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyContribution;

    @Column(name = "projected_balance_at_retirement", precision = 15, scale = 2)
    private BigDecimal projectedBalanceAtRetirement;

    @Column(name = "annual_return_rate", precision = 5, scale = 4)
    private BigDecimal annualReturnRate;

    @Column(name = "is_goal_achievable")
    private Boolean isGoalAchievable;

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

    public RetirementGoal getRetirementGoal() { return retirementGoal; }
    public void setRetirementGoal(RetirementGoal retirementGoal) { this.retirementGoal = retirementGoal; }

    public ScenarioType getScenarioType() { return scenarioType; }
    public void setScenarioType(ScenarioType scenarioType) { this.scenarioType = scenarioType; }

    public BigDecimal getMonthlyContribution() { return monthlyContribution; }
    public void setMonthlyContribution(BigDecimal monthlyContribution) { this.monthlyContribution = monthlyContribution; }

    public BigDecimal getProjectedBalanceAtRetirement() { return projectedBalanceAtRetirement; }
    public void setProjectedBalanceAtRetirement(BigDecimal projectedBalanceAtRetirement) { this.projectedBalanceAtRetirement = projectedBalanceAtRetirement; }

    public BigDecimal getAnnualReturnRate() { return annualReturnRate; }
    public void setAnnualReturnRate(BigDecimal annualReturnRate) { this.annualReturnRate = annualReturnRate; }

    public Boolean getIsGoalAchievable() { return isGoalAchievable; }
    public void setIsGoalAchievable(Boolean isGoalAchievable) { this.isGoalAchievable = isGoalAchievable; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
