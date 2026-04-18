package com.map.retirement.service;

import com.map.retirement.dto.RetirementGoalRequest;
import com.map.retirement.dto.RetirementGoalResponse;
import com.map.retirement.exception.InvalidRetirementGoalException;
import com.map.retirement.model.RetirementGoal;
import com.map.retirement.repository.RetirementGoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for RetirementGoalService
 * Story: MAP-18 — Client Retirement Goal & Savings Calculator
 * Epic:  MAP-5  — Retirement Planning Tools
 *
 * AC Coverage:
 * AC1: Given valid inputs, system calculates and displays required savings target
 * AC2: Given valid inputs, result shows projected monthly savings required
 * AC3: Given invalid inputs (retirement age <= current age), validation error is shown
 */
@ExtendWith(MockitoExtension.class)
class RetirementGoalServiceTest {

    @Mock
    private RetirementGoalRepository retirementGoalRepository;

    @InjectMocks
    private RetirementGoalService retirementGoalService;

    private RetirementGoalRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RetirementGoalRequest();
        validRequest.setCurrentAge(35);
        validRequest.setTargetRetirementAge(65);
        validRequest.setMonthlyIncomeNeeded(new BigDecimal("5000.00"));
        validRequest.setCurrentSavings(new BigDecimal("50000.00"));
    }

    // ─── AC1: Valid inputs → required savings target calculated ──────────────

    @Test
    @DisplayName("MAP-18 AC1: Given valid inputs, calculates required savings target")
    void givenValidInputs_whenSaveRetirementGoal_thenRequiredTargetIsCalculated() {
        RetirementGoal savedGoal = buildGoalEntity(1L, new BigDecimal("1500000.00"), new BigDecimal("3763.89"));
        when(retirementGoalRepository.findTopByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());
        when(retirementGoalRepository.save(any())).thenReturn(savedGoal);

        RetirementGoalResponse response = retirementGoalService.saveRetirementGoal(1L, validRequest);

        assertThat(response.getRequiredSavingsTarget()).isNotNull();
        assertThat(response.getRequiredSavingsTarget()).isPositive();
        verify(retirementGoalRepository).save(any(RetirementGoal.class));
    }

    // ─── AC2: Valid inputs → projected monthly savings required shown ────────

    @Test
    @DisplayName("MAP-18 AC2: Given valid inputs, returns projected monthly savings required")
    void givenValidInputs_whenSaveRetirementGoal_thenProjectedMonthlySavingsReturned() {
        RetirementGoal savedGoal = buildGoalEntity(1L, new BigDecimal("1500000.00"), new BigDecimal("3763.89"));
        when(retirementGoalRepository.findTopByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());
        when(retirementGoalRepository.save(any())).thenReturn(savedGoal);

        RetirementGoalResponse response = retirementGoalService.saveRetirementGoal(1L, validRequest);

        assertThat(response.getProjectedMonthlySavingsRequired()).isNotNull();
        assertThat(response.getProjectedMonthlySavingsRequired()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
    }

    // ─── AC3: Retirement age ≤ current age → validation error ───────────────

    @Test
    @DisplayName("MAP-18 AC3: Given retirement age equals current age, throws validation error")
    void givenRetirementAgeEqualsCurrentAge_whenSaveGoal_thenThrowsException() {
        validRequest.setTargetRetirementAge(35); // same as currentAge

        assertThatThrownBy(() -> retirementGoalService.saveRetirementGoal(1L, validRequest))
                .isInstanceOf(InvalidRetirementGoalException.class)
                .hasMessageContaining("Target retirement age must be greater than current age");
    }

    @Test
    @DisplayName("MAP-18 AC3: Given retirement age less than current age, throws validation error")
    void givenRetirementAgeLessThanCurrentAge_whenSaveGoal_thenThrowsException() {
        validRequest.setTargetRetirementAge(30); // less than currentAge=35

        assertThatThrownBy(() -> retirementGoalService.saveRetirementGoal(1L, validRequest))
                .isInstanceOf(InvalidRetirementGoalException.class);
    }

    @Test
    @DisplayName("MAP-18: If savings gap is zero (already met goal), monthly required is 0")
    void givenSavingsAlreadyMeetsTarget_whenSaveGoal_thenMonthlyRequiredIsZero() {
        validRequest.setCurrentSavings(new BigDecimal("2000000.00")); // exceeds target
        RetirementGoal savedGoal = buildGoalEntity(1L, new BigDecimal("1500000.00"), BigDecimal.ZERO);
        when(retirementGoalRepository.findTopByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());
        when(retirementGoalRepository.save(any())).thenReturn(savedGoal);

        RetirementGoalResponse response = retirementGoalService.saveRetirementGoal(1L, validRequest);

        assertThat(response.getProjectedMonthlySavingsRequired()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("MAP-18: Retrieves all goals for a client")
    void givenClientHasGoals_whenGetGoals_thenReturnsAllGoals() {
        RetirementGoal goal = buildGoalEntity(1L, new BigDecimal("1500000.00"), new BigDecimal("3763.89"));
        when(retirementGoalRepository.findByClientId(1L)).thenReturn(List.of(goal));

        List<RetirementGoalResponse> goals = retirementGoalService.getGoalsByClientId(1L);

        assertThat(goals).hasSize(1);
    }

    // ─── Helper ─────────────────────────────────────────────────────────────

    private RetirementGoal buildGoalEntity(Long id, BigDecimal target, BigDecimal monthly) {
        RetirementGoal g = new RetirementGoal();
        g.setId(id);
        g.setClientId(1L);
        g.setCurrentAge(35);
        g.setTargetRetirementAge(65);
        g.setMonthlyIncomeNeeded(new BigDecimal("5000.00"));
        g.setCurrentSavings(new BigDecimal("50000.00"));
        g.setRequiredSavingsTarget(target);
        g.setProjectedMonthlySavingsRequired(monthly);
        return g;
    }
}
