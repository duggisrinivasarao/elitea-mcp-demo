package com.edwardjones.onboarding.dto;

import com.edwardjones.onboarding.model.FinancialGoal;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO for creating a financial goal.
 * Story Reference: MAP-12 — Set financial goals
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialGoalRequest {

    @NotBlank(message = "Goal name is required")
    private String goalName;

    @NotNull(message = "Goal type is required")
    private FinancialGoal.GoalType goalType;

    @NotNull(message = "Target amount is required")
    @Positive(message = "Target amount must be positive")
    private Double targetAmount;

    @PositiveOrZero(message = "Current savings must be zero or positive")
    private Double currentSavings;

    @NotNull(message = "Target date is required")
    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;

    private FinancialGoal.GoalPriority priority;
    private String notes;
}
