package com.edwardjones.onboarding.service;

import com.edwardjones.onboarding.dto.RiskToleranceRequest;
import com.edwardjones.onboarding.model.Client;
import com.edwardjones.onboarding.model.RiskTolerance;
import com.edwardjones.onboarding.repository.ClientRepository;
import com.edwardjones.onboarding.repository.RiskToleranceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RiskToleranceService.
 * Story Reference: MAP-9 — Risk tolerance questionnaire
 */
@ExtendWith(MockitoExtension.class)
class RiskToleranceServiceTest {

    @Mock
    private RiskToleranceRepository riskToleranceRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private RiskToleranceService riskToleranceService;

    /**
     * AC: Given score 1-2, When questionnaire submitted, Then category is CONSERVATIVE.
     */
    @Test
    @DisplayName("MAP-9 AC1: Score 2 should map to CONSERVATIVE category")
    void shouldMapLowScoreToConservative() {
        Client client = Client.builder().id(1L).build();
        RiskToleranceRequest request = RiskToleranceRequest.builder()
                .score(2)
                .investmentHorizonYears(5)
                .lossTolerancePercentage(5.0)
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(riskToleranceRepository.findByClientId(1L)).thenReturn(Optional.empty());
        when(riskToleranceRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        RiskTolerance result = riskToleranceService.submitQuestionnaire(1L, request);

        assertThat(result.getRiskCategory()).isEqualTo(RiskTolerance.RiskCategory.CONSERVATIVE);
    }

    /**
     * AC: Given score 9-10, When questionnaire submitted, Then category is AGGRESSIVE.
     */
    @Test
    @DisplayName("MAP-9 AC2: Score 9 should map to AGGRESSIVE category")
    void shouldMapHighScoreToAggressive() {
        Client client = Client.builder().id(1L).build();
        RiskToleranceRequest request = RiskToleranceRequest.builder()
                .score(9)
                .investmentHorizonYears(20)
                .lossTolerancePercentage(40.0)
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(riskToleranceRepository.findByClientId(1L)).thenReturn(Optional.empty());
        when(riskToleranceRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        RiskTolerance result = riskToleranceService.submitQuestionnaire(1L, request);

        assertThat(result.getRiskCategory()).isEqualTo(RiskTolerance.RiskCategory.AGGRESSIVE);
    }

    /**
     * AC: Given invalid score (0 or 11), When questionnaire submitted, Then exception thrown.
     */
    @Test
    @DisplayName("MAP-9 AC3: Should throw exception for invalid score")
    void shouldThrowExceptionForInvalidScore() {
        RiskToleranceRequest request = RiskToleranceRequest.builder()
                .score(11)
                .investmentHorizonYears(10)
                .build();

        assertThatThrownBy(() -> riskToleranceService.submitQuestionnaire(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 1 and 10");
    }
}
