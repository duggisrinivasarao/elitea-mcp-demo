package com.map.retirement.service;

import com.map.retirement.dto.ScenarioComparisonResponse;
import com.map.retirement.dto.ScenarioRequest;
import com.map.retirement.dto.ScenarioResponse;
import com.map.retirement.exception.ResourceNotFoundException;
import com.map.retirement.model.RetirementGoal;
import com.map.retirement.model.RetirementScenario;
import com.map.retirement.model.RetirementScenario.ScenarioType;
import com.map.retirement.repository.RetirementGoalRepository;
import com.map.retirement.repository.RetirementScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for RetirementScenarioService
 * Story: MAP-19 — Retirement Scenario Simulation
 * Epic:  MAP-5  — Retirement Planning Tools
 *
 * AC Coverage:
 * AC1: Given scenario type selected, projected retirement balance chart is displayed
 * AC2: Given multiple scenarios run, all three shown side-by-side
 * AC3: Given contribution adjusted, projections update dynamically
 */
@ExtendWith(MockitoExtension.class)
class RetirementScenarioServiceTest {

    @Mock
    private RetirementScenarioRepository scenarioRepository;

    @Mock
    private RetirementGoalRepository goalRepository;

    @InjectMocks
    private RetirementScenarioService scenarioService;

    private RetirementGoal mockGoal;

    @BeforeEach
    void setUp() {
        mockGoal = new RetirementGoal();
        mockGoal.setId(1L);
        mockGoal.setClientId(1L);
        mockGoal.setCurrentAge(35);
        mockGoal.setTargetRetirementAge(65);
        mockGoal.setCurrentSavings(new BigDecimal("50000.00"));
        mockGoal.setRequiredSavingsTarget(new BigDecimal("1500000.00"));
    }

    // ─── AC1: Scenario type selected → projected balance displayed ───────────

    @Test
    @DisplayName("MAP-19 AC1: Given CONSERVATIVE scenario, returns projected retirement balance")
    void givenConservativeScenario_whenRunScenario_thenReturnsProjectedBalance() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(mockGoal));
        when(scenarioRepository.findByClientIdAndRetirementGoalIdAndScenarioType(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(scenarioRepository.save(any())).thenAnswer(inv -> {
            RetirementScenario s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        ScenarioRequest request = new ScenarioRequest();
        request.setScenarioType(ScenarioType.CONSERVATIVE);
        request.setMonthlyContribution(new BigDecimal("1000.00"));

        ScenarioResponse response = scenarioService.runScenario(1L, 1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getProjectedBalanceAtRetirement()).isPositive();
        assertThat(response.getScenarioType()).isEqualTo(ScenarioType.CONSERVATIVE);
        assertThat(response.getAnnualReturnRate()).isEqualByComparingTo(new BigDecimal("0.04"));
    }

    @Test
    @DisplayName("MAP-19 AC1: Given AGGRESSIVE scenario, returns higher projected balance than CONSERVATIVE")
    void givenAggressiveVsConservative_aggressiveYieldsHigherBalance() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(mockGoal));
        when(scenarioRepository.findByClientIdAndRetirementGoalIdAndScenarioType(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(scenarioRepository.save(any())).thenAnswer(inv -> {
            RetirementScenario s = inv.getArgument(0);
            s.setId(2L);
            return s;
        });

        ScenarioRequest conservReq = new ScenarioRequest();
        conservReq.setScenarioType(ScenarioType.CONSERVATIVE);
        conservReq.setMonthlyContribution(new BigDecimal("1000.00"));

        ScenarioRequest aggrReq = new ScenarioRequest();
        aggrReq.setScenarioType(ScenarioType.AGGRESSIVE);
        aggrReq.setMonthlyContribution(new BigDecimal("1000.00"));

        ScenarioResponse conservative = scenarioService.runScenario(1L, 1L, conservReq);
        ScenarioResponse aggressive   = scenarioService.runScenario(1L, 1L, aggrReq);

        assertThat(aggressive.getProjectedBalanceAtRetirement())
                .isGreaterThan(conservative.getProjectedBalanceAtRetirement());
    }

    // ─── AC2: All three scenarios shown side-by-side ─────────────────────────

    @Test
    @DisplayName("MAP-19 AC2: Given comparison request, all three scenarios are returned side-by-side")
    void givenCompareRequest_whenCompareAllScenarios_thenAllThreeReturned() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(mockGoal));
        when(scenarioRepository.findByClientIdAndRetirementGoalIdAndScenarioType(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(scenarioRepository.save(any())).thenAnswer(inv -> {
            RetirementScenario s = inv.getArgument(0);
            s.setId(99L);
            return s;
        });

        ScenarioComparisonResponse comparison =
                scenarioService.compareAllScenarios(1L, 1L, new BigDecimal("1000.00"));

        assertThat(comparison.getScenarios()).hasSize(3);
        assertThat(comparison.getScenarios())
                .extracting(ScenarioResponse::getScenarioType)
                .containsExactlyInAnyOrder(ScenarioType.CONSERVATIVE, ScenarioType.MODERATE, ScenarioType.AGGRESSIVE);
    }

    // ─── AC3: Contribution adjusted → projections update dynamically ─────────

    @Test
    @DisplayName("MAP-19 AC3: Given higher monthly contribution, projected balance increases dynamically")
    void givenHigherContribution_whenRunScenario_thenHigherProjectedBalance() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(mockGoal));
        when(scenarioRepository.findByClientIdAndRetirementGoalIdAndScenarioType(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(scenarioRepository.save(any())).thenAnswer(inv -> {
            RetirementScenario s = inv.getArgument(0);
            s.setId(3L);
            return s;
        });

        ScenarioRequest lowReq = new ScenarioRequest();
        lowReq.setScenarioType(ScenarioType.MODERATE);
        lowReq.setMonthlyContribution(new BigDecimal("500.00"));

        ScenarioRequest highReq = new ScenarioRequest();
        highReq.setScenarioType(ScenarioType.MODERATE);
        highReq.setMonthlyContribution(new BigDecimal("2000.00"));

        ScenarioResponse low  = scenarioService.runScenario(1L, 1L, lowReq);
        ScenarioResponse high = scenarioService.runScenario(1L, 1L, highReq);

        assertThat(high.getProjectedBalanceAtRetirement())
                .isGreaterThan(low.getProjectedBalanceAtRetirement());
    }

    @Test
    @DisplayName("MAP-19: Given non-existent goal ID, throws ResourceNotFoundException")
    void givenNonExistentGoal_whenRunScenario_thenThrowsResourceNotFoundException() {
        when(goalRepository.findById(99L)).thenReturn(Optional.empty());

        ScenarioRequest request = new ScenarioRequest();
        request.setScenarioType(ScenarioType.MODERATE);
        request.setMonthlyContribution(new BigDecimal("1000.00"));

        assertThatThrownBy(() -> scenarioService.runScenario(1L, 99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
