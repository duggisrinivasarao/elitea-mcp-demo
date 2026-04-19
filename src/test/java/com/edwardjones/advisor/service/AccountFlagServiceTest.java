package com.edwardjones.advisor.service;

import com.edwardjones.advisor.dto.AccountFlagRequest;
import com.edwardjones.advisor.model.AccountFlag;
import com.edwardjones.advisor.repository.AccountFlagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountFlagService.
 * Story: MAP-25 — Account Flagging for Priority Review
 * Covers all 3 Acceptance Criteria.
 */
class AccountFlagServiceTest {

    @Mock private AccountFlagRepository flagRepository;
    @InjectMocks private AccountFlagService flagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * AC1: Given advisor clicks flag for priority review,
     * Then account is marked and moved to top of client list.
     */
    @Test
    @DisplayName("MAP-25-AC1: flagging account sets flagged=true with reason and timestamp")
    void flagAccount_shouldSetFlaggedWithReasonAndTimestamp() {
        AccountFlagRequest request = new AccountFlagRequest();
        request.setClientId(1L);
        request.setAdvisorId(2L);
        request.setReason("Unusual market activity detected");

        when(flagRepository.findByClientId(1L)).thenReturn(Optional.empty());
        when(flagRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AccountFlag result = flagService.flagAccount(request);

        assertThat(result.isFlagged()).isTrue();
        assertThat(result.getReason()).isEqualTo("Unusual market activity detected");
        assertThat(result.getFlaggedAt()).isNotNull();
    }

    /**
     * AC2: Given flagged account, When advisor adds reason note,
     * Then reason note is saved and visible.
     */
    @Test
    @DisplayName("MAP-25-AC2: re-flagging an existing account updates reason note")
    void flagAccount_existingFlag_shouldUpdateReason() {
        AccountFlag existing = AccountFlag.builder()
                .id(5L).clientId(1L).advisorId(2L)
                .flagged(false).reason("Old reason")
                .createdAt(LocalDateTime.now())
                .build();

        AccountFlagRequest request = new AccountFlagRequest();
        request.setClientId(1L);
        request.setAdvisorId(2L);
        request.setReason("New urgent concern");

        when(flagRepository.findByClientId(1L)).thenReturn(Optional.of(existing));
        when(flagRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AccountFlag result = flagService.flagAccount(request);

        assertThat(result.getReason()).isEqualTo("New urgent concern");
        assertThat(result.isFlagged()).isTrue();
    }

    /**
     * AC3: Given flagged account has been reviewed,
     * When advisor removes flag, Then account returns to normal status.
     */
    @Test
    @DisplayName("MAP-25-AC3: unflagging account sets flagged=false and resolvedAt")
    void unflagAccount_shouldClearFlagAndSetResolvedAt() {
        AccountFlag existing = AccountFlag.builder()
                .id(5L).clientId(1L).advisorId(2L)
                .flagged(true).reason("Under review")
                .flaggedAt(LocalDateTime.now().minusDays(1))
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        when(flagRepository.findByClientId(1L)).thenReturn(Optional.of(existing));
        when(flagRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AccountFlag result = flagService.unflagAccount(1L);

        assertThat(result.isFlagged()).isFalse();
        assertThat(result.getResolvedAt()).isNotNull();
    }

    /**
     * Edge: Unflagging a non-flagged account throws exception.
     */
    @Test
    @DisplayName("MAP-25-edge: unflagging non-flagged account throws IllegalStateException")
    void unflagAccount_notFlagged_shouldThrow() {
        AccountFlag existing = AccountFlag.builder()
                .id(5L).clientId(1L).advisorId(2L).flagged(false).build();

        when(flagRepository.findByClientId(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> flagService.unflagAccount(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not currently flagged");
    }

    /**
     * Edge: Returns all flagged accounts for an advisor.
     */
    @Test
    @DisplayName("MAP-25-edge: getFlaggedAccounts returns only flagged entries")
    void getFlaggedAccounts_shouldReturnFlaggedOnly() {
        AccountFlag flag = AccountFlag.builder().id(1L).clientId(10L).advisorId(2L).flagged(true).build();
        when(flagRepository.findByAdvisorIdAndFlaggedTrue(2L)).thenReturn(List.of(flag));

        List<AccountFlag> result = flagService.getFlaggedAccounts(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isFlagged()).isTrue();
    }
}
