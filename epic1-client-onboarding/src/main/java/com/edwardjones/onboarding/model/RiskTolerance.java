package com.edwardjones.onboarding.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a client's risk tolerance assessment.
 * Story Reference: MAP-9 — Risk tolerance questionnaire
 */
@Entity
@Table(name = "risk_tolerances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskTolerance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * Score from 1 (very conservative) to 10 (very aggressive)
     */
    @Column(name = "score", nullable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category", nullable = false)
    private RiskCategory riskCategory;

    @Column(name = "investment_horizon_years")
    private Integer investmentHorizonYears;

    @Column(name = "loss_tolerance_percentage")
    private Double lossTolerancePercentage;

    @Column(name = "income_stability")
    private String incomeStability;

    @Column(name = "existing_investments")
    private Boolean existingInvestments;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "assessed_at", updatable = false)
    private LocalDateTime assessedAt;

    /**
     * Risk category derived from questionnaire score.
     */
    public enum RiskCategory {
        CONSERVATIVE,
        MODERATELY_CONSERVATIVE,
        MODERATE,
        MODERATELY_AGGRESSIVE,
        AGGRESSIVE
    }

    /**
     * Derives the risk category from the numeric score.
     * @param score integer score from 1 to 10
     * @return appropriate RiskCategory
     */
    public static RiskCategory deriveCategory(int score) {
        if (score <= 2) return RiskCategory.CONSERVATIVE;
        if (score <= 4) return RiskCategory.MODERATELY_CONSERVATIVE;
        if (score <= 6) return RiskCategory.MODERATE;
        if (score <= 8) return RiskCategory.MODERATELY_AGGRESSIVE;
        return RiskCategory.AGGRESSIVE;
    }
}
