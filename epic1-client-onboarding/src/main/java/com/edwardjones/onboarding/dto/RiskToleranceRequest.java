package com.edwardjones.onboarding.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for risk tolerance questionnaire submission.
 * Story Reference: MAP-9 — Risk tolerance questionnaire
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskToleranceRequest {

    @NotNull(message = "Risk score is required")
    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 10, message = "Score must not exceed 10")
    private Integer score;

    @NotNull(message = "Investment horizon is required")
    @Positive(message = "Investment horizon must be positive")
    private Integer investmentHorizonYears;

    @DecimalMin(value = "0.0", message = "Loss tolerance must be 0 or more")
    @DecimalMax(value = "100.0", message = "Loss tolerance must not exceed 100%")
    private Double lossTolerancePercentage;

    private String incomeStability;
    private Boolean existingInvestments;
    private String notes;
}
