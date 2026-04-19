package com.edwardjones.portfolio.service;

import com.edwardjones.portfolio.dto.AdvisorDashboardResponse;
import com.edwardjones.portfolio.model.Holding;
import com.edwardjones.portfolio.repository.HoldingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdvisorDashboardService.
 * Story Reference: MAP-16 — Advisor Consolidated Dashboard
 */
class AdvisorDashboardServiceTest {

    @Mock
    private HoldingRepository holdingRepository;

    @InjectMocks
    private AdvisorDashboardService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    // AC: All assigned clients listed with portfolio value and performance
    @Test
    void getDashboard_shouldListAllAssignedClients() {
        when(holdingRepository.findByClientIds(List.of("C1", "C2")))
                .thenReturn(List.of(
                        makeHolding("C1", new BigDecimal("50000"), new BigDecimal("100")),
                        makeHolding("C2", new BigDecimal("80000"), new BigDecimal("200"))
                ));

        AdvisorDashboardResponse response = service.getDashboard("ADV1", List.of("C1", "C2"), "value");

        assertThat(response.getClients()).hasSize(2);
        assertThat(response.getTotalClients()).isEqualTo(2);
        assertThat(response.getTotalAUM()).isEqualByComparingTo(new BigDecimal("130000"));
    }

    // AC: Sort by performance reorders the list
    @Test
    void getDashboard_shouldSortByPerformanceDescending() {
        when(holdingRepository.findByClientIds(anyList()))
                .thenReturn(List.of(
                        makeHolding("C1", new BigDecimal("10000"), new BigDecimal("100")),   // 1% change
                        makeHolding("C2", new BigDecimal("10000"), new BigDecimal("500"))    // 5% change
                ));

        AdvisorDashboardResponse response = service.getDashboard("ADV1", List.of("C1", "C2"), "performance");

        // C2 has higher daily change, should come first (or flagged first)
        List<String> orderedIds = response.getClients().stream()
                .map(AdvisorDashboardResponse.ClientPortfolioSummary::getClientId).toList();
        assertThat(orderedIds.get(0)).isEqualTo("C2");
    }

    // AC: Flagged accounts (>=5% change) appear at the top
    @Test
    void getDashboard_shouldSurfaceFlaggedAccountsFirst() {
        // C1 = 10% change (flagged), C2 = 1% change (not flagged)
        when(holdingRepository.findByClientIds(anyList()))
                .thenReturn(List.of(
                        makeHolding("C1", new BigDecimal("10000"), new BigDecimal("1000")),  // 10% flagged
                        makeHolding("C2", new BigDecimal("10000"), new BigDecimal("100"))    // 1% normal
                ));

        AdvisorDashboardResponse response = service.getDashboard("ADV1", List.of("C1", "C2"), "value");

        assertThat(response.getClients().get(0).getClientId()).isEqualTo("C1");
        assertThat(response.getClients().get(0).isFlagged()).isTrue();
        assertThat(response.getClients().get(0).getFlagReason()).isNotBlank();
    }

    // AC: Empty client list returns empty dashboard
    @Test
    void getDashboard_shouldReturnEmptyForNoClients() {
        AdvisorDashboardResponse response = service.getDashboard("ADV1", List.of(), "value");

        assertThat(response.getClients()).isEmpty();
        assertThat(response.getTotalAUM()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // AC: Blank advisorId throws
    @Test
    void getDashboard_shouldThrowForBlankAdvisorId() {
        assertThatThrownBy(() -> service.getDashboard("", List.of("C1"), "value"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Holding makeHolding(String clientId, BigDecimal value, BigDecimal dailyChange) {
        Holding h = new Holding();
        h.setClientId(clientId);
        h.setTicker("XX");
        h.setName("Test");
        h.setAssetType(Holding.AssetType.STOCK);
        h.setCurrentValue(value);
        h.setDailyChange(dailyChange);
        h.setQuantity(BigDecimal.ONE);
        h.setCurrentPrice(value);
        return h;
    }
}
