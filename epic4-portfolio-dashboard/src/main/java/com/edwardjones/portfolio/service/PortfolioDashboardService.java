package com.edwardjones.portfolio.service;

import com.edwardjones.portfolio.dto.PortfolioDashboardResponse;
import com.edwardjones.portfolio.model.Holding;
import com.edwardjones.portfolio.repository.HoldingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementing real-time portfolio dashboard logic.
 * Story Reference: MAP-13 — Real-time Portfolio Holdings Dashboard
 */
@Service
public class PortfolioDashboardService {

    private static final int STALE_DATA_THRESHOLD_MINUTES = 30;
    private static final String STALE_DATA_MSG = "Market data is currently unavailable. Displaying last known values as of %s.";

    private final HoldingRepository holdingRepository;

    public PortfolioDashboardService(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
    }

    /**
     * Retrieves the full portfolio dashboard for a given client.
     * AC: All current holdings are displayed with real-time prices and values.
     * AC: Portfolio total value and daily gain/loss are prominently shown.
     * AC: On market data outage, last known data is shown with a timestamp and outage notice.
     *
     * @param clientId the client's identifier
     * @return populated PortfolioDashboardResponse
     * @throws IllegalArgumentException if clientId is blank
     */
    public PortfolioDashboardResponse getDashboard(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client ID must not be blank");
        }

        List<Holding> holdings = holdingRepository.findByClientId(clientId);

        BigDecimal totalValue = holdings.stream()
                .map(Holding::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDailyChange = holdings.stream()
                .map(h -> h.getDailyChange() != null ? h.getDailyChange() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal dailyChangePercent = totalValue.compareTo(BigDecimal.ZERO) > 0
                ? totalDailyChange.divide(totalValue.subtract(totalDailyChange), 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        boolean isStale = holdings.stream().anyMatch(Holding::isDataStale);
        LocalDateTime lastUpdated = holdings.stream()
                .map(Holding::getLastUpdated)
                .filter(dt -> dt != null)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        PortfolioDashboardResponse response = new PortfolioDashboardResponse();
        response.setClientId(clientId);
        response.setTotalValue(totalValue);
        response.setDailyGainLoss(totalDailyChange);
        response.setDailyGainLossPercent(dailyChangePercent.setScale(4, RoundingMode.HALF_UP));
        response.setLastUpdated(lastUpdated);
        response.setDataStale(isStale);
        if (isStale) {
            response.setStaleDataMessage(String.format(STALE_DATA_MSG, lastUpdated));
        }
        response.setHoldings(holdings.stream().map(this::toHoldingDto).collect(Collectors.toList()));
        return response;
    }

    private PortfolioDashboardResponse.HoldingDto toHoldingDto(Holding h) {
        PortfolioDashboardResponse.HoldingDto dto = new PortfolioDashboardResponse.HoldingDto();
        dto.setId(h.getId());
        dto.setTicker(h.getTicker());
        dto.setName(h.getName());
        dto.setAssetType(h.getAssetType().name());
        dto.setQuantity(h.getQuantity());
        dto.setCurrentPrice(h.getCurrentPrice());
        dto.setCurrentValue(h.getCurrentValue());
        dto.setDailyChange(h.getDailyChange());
        dto.setDailyChangePercent(h.getDailyChangePercent());
        return dto;
    }
}
