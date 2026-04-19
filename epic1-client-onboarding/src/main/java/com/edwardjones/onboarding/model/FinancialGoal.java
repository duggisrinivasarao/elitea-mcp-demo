package com.edwardjones.onboarding.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a client's financial goal.
 * Story Reference: MAP-12 — Set financial goals
 */
@Entity
@Table(name = "financial_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @NotBlank
    @Column(name = "goal_name", nullable = false)
    private String goalName;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false)
    private GoalType goalType;

    @NotNull
    @Column(name = "target_amount", nullable = false)
    private Double targetAmount;

    @Column(name = "current_savings")
    private Double currentSavings;

    @NotNull
    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private GoalPriority priority;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private GoalStatus status = GoalStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Types of financial goals available at Edward Jones.
     */
    public enum GoalType {
        RETIREMENT,
        EDUCATION,
        WEALTH_GROWTH,
        HOME_PURCHASE,
        EMERGENCY_FUND,
        OTHER
    }

    /**
     * Priority levels for financial goals.
     */
    public enum GoalPriority {
        HIGH,
        MEDIUM,
        LOW
    }

    /**
     * Status of the financial goal.
     */
    public enum GoalStatus {
        ACTIVE,
        ACHIEVED,
        PAUSED,
        CANCELLED
    }
}
