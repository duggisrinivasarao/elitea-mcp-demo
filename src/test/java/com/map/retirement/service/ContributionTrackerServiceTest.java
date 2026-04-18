package com.map.retirement.service;

import com.map.retirement.dto.ContributionTrackerRequest;
import com.map.retirement.dto.ContributionTrackerResponse;
import com.map.retirement.model.ContributionTracker;
import com.map.retirement.model.ContributionTracker.AccountType;
import com.map.retirement.repository.ContributionTrackerRepository;
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
 * Unit Tests for ContributionTrackerService
 * Story: MAP-21 — IRA/401(k) Contribution Progress Tracker
 * Epic:  MAP-5  — Retirement Planning Tools
 *
 * AC Coverage:
 * AC1: YTD contributions shown as progress bar against annual IRS limit
 * AC2: When 80% limit reached, notification is sent
 * AC3: Annual IRS limits can be updated (new year)
 */
@ExtendWith(MockitoExtension.class)
class ContributionTrackerServiceTest {

    @Mock
    private ContributionTrackerRepository trackerRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ContributionTrackerService trackerService;

    private ContributionTracker existingTracker;

    @BeforeEach
    void setUp() {
        existingTracker = new ContributionTracker();
        existingTracker.setId(1L);
        existingTracker.setClientId(1L);
        existingTracker.setAccountType(AccountType.IRA);
        existingTracker.setTaxYear(java.time.LocalDate.now().getYear());
        existingTracker.setYtdContributions(new BigDecimal("3000.00"));
        existingTracker.setAnnualIrsLimit(new BigDecimal("7000.00"));
        existingTracker.setContributionPercentage(new BigDecimal("42.86"));
        existingTracker.setLimitNotificationSent(false);
    }

    // ─── AC1: YTD contributions shown as percentage against IRS limit ────────

    @Test
    @DisplayName("MAP-21 AC1: Given IRA account, contributions shown as percentage of IRS limit")
    void givenLinkedAccount_whenUpdateContribution_thenPercentageCalculated() {
        ContributionTrackerRequest request = new ContributionTrackerRequest();
        request.setAccountType(AccountType.IRA);
        request.setYtdContributions(new BigDecimal("3500.00"));

        when(trackerRepository.findByClientIdAndAccountTypeAndTaxYear(any(), any(), any()))
                .thenReturn(Optional.of(existingTracker));
        when(trackerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ContributionTrackerResponse response = trackerService.updateContribution(1L, request);

        assertThat(response.getContributionPercentage()).isNotNull();
        assertThat(response.getContributionPercentage()).isPositive();
        // 3500 / 7000 = 50%
        assertThat(response.getContributionPercentage()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("MAP-21 AC1: Retrieves all trackers for current tax year")
    void givenClientWithTrackers_whenGetTrackers_thenReturnsCurrent() {
        when(trackerRepository.findByClientId(1L)).thenReturn(List.of(existingTracker));

        List<ContributionTrackerResponse> trackers = trackerService.getTrackersByClient(1L);

        assertThat(trackers).hasSize(1);
        assertThat(trackers.get(0).getAccountType()).isEqualTo(AccountType.IRA);
    }

    // ─── AC2: 80% threshold reached → notification sent ─────────────────────

    @Test
    @DisplayName("MAP-21 AC2: Given 80%+ contributions reached, notification is sent")
    void givenContributionsAt80Percent_whenUpdate_thenNotificationSent() {
        ContributionTrackerRequest request = new ContributionTrackerRequest();
        request.setAccountType(AccountType.IRA);
        request.setYtdContributions(new BigDecimal("5600.00")); // 5600/7000 = 80%

        when(trackerRepository.findByClientIdAndAccountTypeAndTaxYear(any(), any(), any()))
                .thenReturn(Optional.of(existingTracker));
        when(trackerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        trackerService.updateContribution(1L, request);

        verify(notificationService).sendContributionLimitAlert(
                eq(1L), eq(AccountType.IRA), any(BigDecimal.class));
    }

    @Test
    @DisplayName("MAP-21 AC2: Given notification already sent, no duplicate notification")
    void givenNotificationAlreadySent_whenUpdate_thenNoSecondNotification() {
        existingTracker.setLimitNotificationSent(true);

        ContributionTrackerRequest request = new ContributionTrackerRequest();
        request.setAccountType(AccountType.IRA);
        request.setYtdContributions(new BigDecimal("6000.00")); // >80%

        when(trackerRepository.findByClientIdAndAccountTypeAndTaxYear(any(), any(), any()))
                .thenReturn(Optional.of(existingTracker));
        when(trackerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        trackerService.updateContribution(1L, request);

        verify(notificationService, never()).sendContributionLimitAlert(any(), any(), any());
    }

    @Test
    @DisplayName("MAP-21 AC2: Given contributions below 80%, no notification sent")
    void givenContributionsBelow80Percent_whenUpdate_thenNoNotification() {
        ContributionTrackerRequest request = new ContributionTrackerRequest();
        request.setAccountType(AccountType.IRA);
        request.setYtdContributions(new BigDecimal("4000.00")); // 57% — below threshold

        when(trackerRepository.findByClientIdAndAccountTypeAndTaxYear(any(), any(), any()))
                .thenReturn(Optional.of(existingTracker));
        when(trackerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        trackerService.updateContribution(1L, request);

        verify(notificationService, never()).sendContributionLimitAlert(any(), any(), any());
    }

    // ─── AC3: IRS limits updated for new year ────────────────────────────────

    @Test
    @DisplayName("MAP-21 AC3: Given new IRS limit, annual limits are updated in the system")
    void givenNewIrsLimit_whenUpdateAnnualLimits_thenReturnsUpdateCount() {
        when(trackerRepository.updateAnnualLimitByAccountTypeAndYear(
                AccountType.IRA, new BigDecimal("7500.00"), 2025)).thenReturn(5);

        int updated = trackerService.updateAnnualIrsLimits(AccountType.IRA, new BigDecimal("7500.00"), 2025);

        assertThat(updated).isEqualTo(5);
        verify(trackerRepository).updateAnnualLimitByAccountTypeAndYear(
                AccountType.IRA, new BigDecimal("7500.00"), 2025);
    }
}
