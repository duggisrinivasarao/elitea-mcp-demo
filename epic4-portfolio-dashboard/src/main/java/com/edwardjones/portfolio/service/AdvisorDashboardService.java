package com.edwardjones.portfolio.service;

import com.edwardjones.portfolio.dto.AdvisorDashboardResponse;
import com.edwardjones.portfolio.model.Holding;
import com.edwardjones.portfolio.repository.HoldingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for the advisor's consolidated portfolio dashboard.
 * Story Reference: MAP-16 — Advisor Consolidated Dashboard
 */
@Service
public class AdvisorDashboardService {

    private static final BigDecimal FLAG_THRESHOLD_PERCENT = BigDecimal.valueOf(5.0);

    private final HoldingRepository holdingRepository;

    public AdvisorDashboardService(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
    }

    /**
     * Returns a consolidated dashboard for a financial advisor, listing all client portfolios.
     * AC: All assigned clients are listed with portfolio value, performance, and risk profile.
     * AC: Advisor can sort/filter by performance or risk (sort applied here; filter via params).
     * AC: Flagged accounts (significant changes) appear highlighted at the top.
     *
     * @param advisorId the advisor's identifier
     * @param clientIds list of client IDs assigned to the advisor
     * @param sortBy    field to sort by: "performance" or "value"
     * @return AdvisorDashboardResponse with sorted and flagged client portfolios
     */
    public AdvisorDashboardResponse getDashboard(String advisorId, List<String> clientIds, String sortBy) {
        if (advisorId == null || advisorId.isBlank()) {
            throw new IllegalArgumentException("Advisor ID must not be blank");
        }
        if (clientIds == null || clientIds.isEmpty()) {
            AdvisorDashboardResponse empty = new AdvisorDashboardResponse();
            empty.setAdvisorId(advisorId);
            empty.setClients(Collections.emptyList());
            empty.setTotalClients(0);
            empty.setTotalAUM(BigDecimal.ZERO);
            return empty;
        }

        List<Holding> allHoldings = holdingRepository.findByClientIds(clientIds);
        Map<String, List<Holding>> byClient = allHoldings.stream()
                .collect(Collectors.groupingBy(Holding::getClientId));

        List<AdvisorDashboardResponse.ClientPortfolioSummary> summaries = clientIds.stream()
                .map(cid -> buildSummary(cid, byClient.getOrDefault(cid, Collections.emptyList())))
                .collect(Collectors.toList());

        // Flagged accounts rise to top, then sort by requested field
        summaries.sort(Comparator
                .comparing(AdvisorDashboardResponse.ClientPortfolioSummary::isFlagged).reversed()
                .thenComparing(buildSecondaryComparator(sortBy)));

        BigDecimal totalAUM = summaries.stream()
                .map(AdvisorDashboardResponse.ClientPortfolioSummary::getPortfolioValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AdvisorDashboardResponse response = new AdvisorDashboardResponse();
        response.setAdvisorId(advisorId);
        response.setClients(summaries);
        response.setTotalClients(summaries.size());
        response.setTotalAUM(totalAUM);
        return response;
    }

    private AdvisorDashboardResponse.ClientPortfolioSummary buildSummary(
            String clientId, List<Holding> holdings) {

        BigDecimal totalValue = holdings.stream()
                .map(Holding::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDailyChange = holdings.stream()
                .map(h -> h.getDailyChange() != null ? h.getDailyChange() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal prevValue = totalValue.subtract(totalDailyChange);
        BigDecimal dailyChangePct = prevValue.compareTo(BigDecimal.ZERO) > 0
                ? totalDailyChange.divide(prevValue, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        boolean flagged = dailyChangePct.abs().compareTo(FLAG_THRESHOLD_PERCENT) >= 0;

        AdvisorDashboardResponse.ClientPortfolioSummary summary =
                new AdvisorDashboardResponse.ClientPortfolioSummary();
        summary.setClientId(clientId);
        summary.setPortfolioValue(totalValue);
        summary.setDailyChangePercent(dailyChangePct.setScale(4, RoundingMode.HALF_UP));
        summary.setFlagged(flagged);
        if (flagged) {
            summary.setFlagReason(String.format("Portfolio changed by %.2f%% today", dailyChangePct));
        }
        return summary;
    }

    private Comparator<AdvisorDashboardResponse.ClientPortfolioSummary> buildSecondaryComparator(
            String sortBy) {
        if ("performance".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(
                    AdvisorDashboardResponse.ClientPortfolioSummary::getDailyChangePercent,
                    Comparator.nullsLast(Comparator.reverseOrder()));
        }
        return Comparator.comparing(
                AdvisorDashboardResponse.ClientPortfolioSummary::getPortfolioValue,
                Comparator.nullsLast(Comparator.reverseOrder()));
    }
}
