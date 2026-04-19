package com.edwardjones.portfolio.service;

import com.edwardjones.portfolio.dto.PortfolioDashboardResponse;
import com.edwardjones.portfolio.model.Holding;
import com.edwardjones.portfolio.repository.HoldingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PortfolioDashboardService.
 * Story Reference: MAP-13 — Real-time Portfolio Holdings Dashboard
 */
class PortfolioDashboardServiceTest {

    @Mock
    private HoldingRepository holdingRepository;

    @InjectMocks
    private PortfolioDashboardService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    // AC: All current holdings are displayed with real-time prices and values
    @Test
    void getDashboard_shouldReturnAllHoldingsForClient() {
        Holding h1 = holding("CLIENT1", "AAPL", Holding.AssetType.STOCK,
                new BigDecimal("100.00"), new BigDecimal("150.00"), false);
        Holding h2 = holding("CLIENT1", "BND", Holding.AssetType.BOND,
                new BigDecimal("50.00"), new BigDecimal("0.00"), false);

        when(holdingRepository.findByClientId("CLIENT1")).thenReturn(List.of(h1, h2));

        PortfolioDashboardResponse response = service.getDashboard("CLIENT1");

        assertThat(response.getHoldings()).hasSize(2);
        assertThat(response.getHoldings()).extracting("ticker").containsExactlyInAnyOrder("AAPL", "BND");
    }

    // AC: Portfolio total value and daily gain/loss are prominently shown
    @Test
    void getDashboard_shouldCalculateTotalValueAndDailyGainLoss() {
        Holding h1 = holding("CLIENT1", "AAPL", Holding.AssetType.STOCK,
                new BigDecimal("10000.00"), new BigDecimal("200.00"), false);
        Holding h2 = holding("CLIENT1", "MSFT", Holding.AssetType.STOCK,
                new BigDecimal("5000.00"), new BigDecimal("100.00"), false);

        when(holdingRepository.findByClientId("CLIENT1")).thenReturn(List.of(h1, h2));

        PortfolioDashboardResponse response = service.getDashboard("CLIENT1");

        assertThat(response.getTotalValue()).isEqualByComparingTo(new BigDecimal("15000.00"));
        assertThat(response.getDailyGainLoss()).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    // AC: On market data outage, last known data is shown with timestamp and outage notice
    @Test
    void getDashboard_shouldFlagStaleDataWithMessage() {
        Holding stale = holding("CLIENT2", "GOOG", Holding.AssetType.STOCK,
                new BigDecimal("20000.00"), new BigDecimal("0.00"), true);
        stale.setLastUpdated(LocalDateTime.now().minusHours(2));

        when(holdingRepository.findByClientId("CLIENT2")).thenReturn(List.of(stale));

        PortfolioDashboardResponse response = service.getDashboard("CLIENT2");

        assertThat(response.isDataStale()).isTrue();
        assertThat(response.getStaleDataMessage()).isNotBlank();
        assertThat(response.getLastUpdated()).isNotNull();
    }

    // AC: Blank clientId throws IllegalArgumentException
    @Test
    void getDashboard_shouldThrowForBlankClientId() {
        assertThatThrownBy(() -> service.getDashboard(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Client ID");
    }

    // AC: Empty portfolio returns zero totals
    @Test
    void getDashboard_shouldReturnZeroTotalsForEmptyPortfolio() {
        when(holdingRepository.findByClientId("EMPTY")).thenReturn(List.of());

        PortfolioDashboardResponse response = service.getDashboard("EMPTY");

        assertThat(response.getTotalValue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getDailyGainLoss()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // Helper
    private Holding holding(String clientId, String ticker, Holding.AssetType type,
                             BigDecimal value, BigDecimal dailyChange, boolean stale) {
        Holding h = new Holding();
        h.setClientId(clientId);
        h.setTicker(ticker);
        h.setName(ticker + " Inc");
        h.setAssetType(type);
        h.setCurrentValue(value);
        h.setDailyChange(dailyChange);
        h.setCurrentPrice(value);
        h.setQuantity(BigDecimal.ONE);
        h.setDataStale(stale);
        h.setLastUpdated(LocalDateTime.now());
        return h;
    }
}
