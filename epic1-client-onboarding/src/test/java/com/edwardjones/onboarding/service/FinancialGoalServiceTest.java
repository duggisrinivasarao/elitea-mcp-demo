package com.edwardjones.onboarding.service;

import com.edwardjones.onboarding.dto.FinancialGoalRequest;
import com.edwardjones.onboarding.model.Client;
import com.edwardjones.onboarding.model.FinancialGoal;
import com.edwardjones.onboarding.repository.ClientRepository;
import com.edwardjones.onboarding.repository.FinancialGoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FinancialGoalService.
 * Story Reference: MAP-12 — Set financial goals
 */
@ExtendWith(MockitoExtension.class)
class FinancialGoalServiceTest {

    @Mock
    private FinancialGoalRepository goalRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private FinancialGoalService financialGoalService;

    private Client testClient;
    private FinancialGoalRequest validRequest;

    @BeforeEach
    void setUp() {
        testClient = Client.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .status(Client.ClientStatus.ACTIVE)
                .build();

        validRequest = FinancialGoalRequest.builder()
                .goalName("Retirement Fund")
                .goalType(FinancialGoal.GoalType.RETIREMENT)
                .targetAmount(500000.0)
                .currentSavings(50000.0)
                .targetDate(LocalDate.now().plusYears(20))
                .priority(1)
                .notes("Primary retirement goal")
                .build();
    }

    /**
     * AC1: Given a client is in their profile,
     * When they select a predefined goal type and save it,
     * Then the goal is created and set to ACTIVE status.
     */
    @Test
    @DisplayName("MAP-12 AC1: Should create financial goal with ACTIVE status successfully")
    void shouldCreateFinancialGoalSuccessfully() {
        FinancialGoal savedGoal = FinancialGoal.builder()
                .id(1L)
                .client(testClient)
                .goalName("Retirement Fund")
                .goalType(FinancialGoal.GoalType.RETIREMENT)
                .targetAmount(500000.0)
                .currentSavings(50000.0)
                .targetDate(LocalDate.now().plusYears(20))
                .status(FinancialGoal.GoalStatus.ACTIVE)
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(goalRepository.save(any(FinancialGoal.class))).thenReturn(savedGoal);

        FinancialGoal result = financialGoalService.createGoal(1L, validRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getGoalName()).isEqualTo("Retirement Fund");
        assertThat(result.getGoalType()).isEqualTo(FinancialGoal.GoalType.RETIREMENT);
        assertThat(result.getStatus()).isEqualTo(FinancialGoal.GoalStatus.ACTIVE);
        assertThat(result.getTargetAmount()).isEqualTo(500000.0);
        verify(goalRepository, times(1)).save(any(FinancialGoal.class));
    }

    /**
     * AC2: Given a client sets a goal with a target amount and date,
     * When they save it, Then the goal is visible to their advisor.
     * (Verified by confirming goal is queryable by clientId.)
     */
    @Test
    @DisplayName("MAP-12 AC2: Should retrieve all active goals for a client")
    void shouldReturnActiveGoalsForClient() {
        List<FinancialGoal> activeGoals = List.of(
                FinancialGoal.builder()
                        .id(1L)
                        .goalName("Retirement Fund")
                        .goalType(FinancialGoal.GoalType.RETIREMENT)
                        .status(FinancialGoal.GoalStatus.ACTIVE)
                        .build(),
                FinancialGoal.builder()
                        .id(2L)
                        .goalName("Education Fund")
                        .goalType(FinancialGoal.GoalType.EDUCATION)
                        .status(FinancialGoal.GoalStatus.ACTIVE)
                        .build()
        );

        when(goalRepository.findByClientIdAndStatus(1L, FinancialGoal.GoalStatus.ACTIVE))
                .thenReturn(activeGoals);

        List<FinancialGoal> result = financialGoalService.getActiveGoals(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(FinancialGoal::getGoalType)
                .containsExactlyInAnyOrder(
                        FinancialGoal.GoalType.RETIREMENT,
                        FinancialGoal.GoalType.EDUCATION
                );
        verify(goalRepository, times(1)).findByClientIdAndStatus(1L, FinancialGoal.GoalStatus.ACTIVE);
    }

    /**
     * AC3: Given a client wants to update goals,
     * When they edit and save, Then updated goals reflect immediately without data loss.
     */
    @Test
    @DisplayName("MAP-12 AC3: Should update goal savings progress without data loss")
    void shouldUpdateGoalProgressSuccessfully() {
        FinancialGoal existingGoal = FinancialGoal.builder()
                .id(1L)
                .client(testClient)
                .goalName("Retirement Fund")
                .goalType(FinancialGoal.GoalType.RETIREMENT)
                .targetAmount(500000.0)
                .currentSavings(50000.0)
                .status(FinancialGoal.GoalStatus.ACTIVE)
                .build();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(existingGoal));
        when(goalRepository.save(any(FinancialGoal.class))).thenAnswer(inv -> inv.getArguments()[0]);

        FinancialGoal updated = financialGoalService.updateGoalProgress(1L, 150000.0);

        assertThat(updated.getCurrentSavings()).isEqualTo(150000.0);
        assertThat(updated.getGoalName()).isEqualTo("Retirement Fund"); // data not lost
        assertThat(updated.getStatus()).isEqualTo(FinancialGoal.GoalStatus.ACTIVE);
        verify(goalRepository, times(1)).save(existingGoal);
    }

    /**
     * AC4: Given currentSavings reaches or exceeds the targetAmount,
     * When progress is updated, Then goal status automatically changes to ACHIEVED.
     */
    @Test
    @DisplayName("MAP-12 AC4: Should mark goal as ACHIEVED when savings meet target")
    void shouldMarkGoalAsAchievedWhenTargetMet() {
        FinancialGoal existingGoal = FinancialGoal.builder()
                .id(1L)
                .client(testClient)
                .goalName("Retirement Fund")
                .goalType(FinancialGoal.GoalType.RETIREMENT)
                .targetAmount(500000.0)
                .currentSavings(400000.0)
                .status(FinancialGoal.GoalStatus.ACTIVE)
                .build();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(existingGoal));
        when(goalRepository.save(any(FinancialGoal.class))).thenAnswer(inv -> inv.getArguments()[0]);

        FinancialGoal updated = financialGoalService.updateGoalProgress(1L, 500000.0);

        assertThat(updated.getCurrentSavings()).isEqualTo(500000.0);
        assertThat(updated.getStatus()).isEqualTo(FinancialGoal.GoalStatus.ACHIEVED);
    }

    /**
     * AC5: Given an invalid target amount (zero or negative),
     * When client tries to create a goal, Then an error is thrown.
     */
    @Test
    @DisplayName("MAP-12 AC5: Should throw exception for invalid target amount")
    void shouldThrowExceptionForInvalidTargetAmount() {
        FinancialGoalRequest invalidRequest = FinancialGoalRequest.builder()
                .goalName("Bad Goal")
                .goalType(FinancialGoal.GoalType.WEALTH_GROWTH)
                .targetAmount(-1000.0)
                .targetDate(LocalDate.now().plusYears(5))
                .build();

        assertThatThrownBy(() -> financialGoalService.createGoal(1L, invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");

        verify(goalRepository, never()).save(any());
    }

    /**
     * AC6: Given a target date in the past,
     * When client tries to create a goal, Then an error is thrown.
     */
    @Test
    @DisplayName("MAP-12 AC6: Should throw exception when target date is in the past")
    void shouldThrowExceptionForPastTargetDate() {
        FinancialGoalRequest pastDateRequest = FinancialGoalRequest.builder()
                .goalName("Past Goal")
                .goalType(FinancialGoal.GoalType.EDUCATION)
                .targetAmount(10000.0)
                .targetDate(LocalDate.now().minusYears(1)) // past date
                .build();

        assertThatThrownBy(() -> financialGoalService.createGoal(1L, pastDateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("future");

        verify(goalRepository, never()).save(any());
    }

    /**
     * AC7: Given a non-existent client ID,
     * When goal creation is attempted, Then EntityNotFoundException is thrown.
     */
    @Test
    @DisplayName("MAP-12 AC7: Should throw EntityNotFoundException for unknown client")
    void shouldThrowExceptionForUnknownClient() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> financialGoalService.createGoal(999L, validRequest))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(goalRepository, never()).save(any());
    }
}
