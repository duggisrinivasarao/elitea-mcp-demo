package com.edwardjones.portfolio.service;

import com.edwardjones.portfolio.dto.PerformanceChartResponse;
import com.edwardjones.portfolio.model.PerformanceSnapshot;
import com.edwardjones.portfolio.repository.PerformanceSnapshotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for providing portfolio performance chart data with time-range filtering.
 * Story Reference: MAP-14 — Performance Charts over Time
 */
@Service
public class PerformanceChartService {

    public enum TimeRange { ONE_MONTH, THREE_MONTHS, SIX_MONTHS, ONE_YEAR, ALL }

    private static final String INSUFFICIENT_DATA_MSG =
            "Insufficient historical data for the selected time range. Displaying all available data.";

    private final PerformanceSnapshotRepository snapshotRepository;

    public PerformanceChartService(PerformanceSnapshotRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;
    }

    /**
     * Returns performance chart data for a given client and time range.
     * AC: Chart updates to reflect the selected period (1M, 3M, 6M, 1Y, All).
     * AC: Exact portfolio value and date are available per data point (for tooltip).
     * AC: If insufficient data, only available data is shown with an informational message.
     *
     * @param clientId  the client's identifier
     * @param timeRange the requested time range
     * @return PerformanceChartResponse with data points
     */
    public PerformanceChartResponse getChartData(String clientId, TimeRange timeRange) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client ID must not be blank");
        }

        LocalDate today = LocalDate.now();
        LocalDate fromDate = resolveFromDate(today, timeRange);

        List<PerformanceSnapshot> snapshots;
        boolean insufficientData = false;

        if (timeRange == TimeRange.ALL) {
            snapshots = snapshotRepository.findByClientIdOrderBySnapshotDateAsc(clientId);
        } else {
            snapshots = snapshotRepository
                    .findByClientIdAndSnapshotDateBetweenOrderBySnapshotDateAsc(clientId, fromDate, today);

            // Check if we got fewer data points than expected
            long expectedDays = java.time.temporal.ChronoUnit.DAYS.between(fromDate, today);
            if (snapshots.size() < expectedDays * 0.5) {
                insufficientData = true;
            }
        }

        PerformanceChartResponse response = new PerformanceChartResponse();
        response.setClientId(clientId);
        response.setTimeRange(timeRange.name());
        response.setInsufficientData(insufficientData);
        if (insufficientData) {
            response.setInformationalMessage(INSUFFICIENT_DATA_MSG);
        }
        response.setDataPoints(snapshots.stream().map(this::toDataPoint).collect(Collectors.toList()));
        return response;
    }

    private LocalDate resolveFromDate(LocalDate today, TimeRange range) {
        return switch (range) {
            case ONE_MONTH -> today.minusMonths(1);
            case THREE_MONTHS -> today.minusMonths(3);
            case SIX_MONTHS -> today.minusMonths(6);
            case ONE_YEAR -> today.minusYears(1);
            default -> LocalDate.of(2000, 1, 1);
        };
    }

    private PerformanceChartResponse.DataPoint toDataPoint(PerformanceSnapshot s) {
        PerformanceChartResponse.DataPoint dp = new PerformanceChartResponse.DataPoint();
        dp.setDate(s.getSnapshotDate());
        dp.setTotalValue(s.getTotalValue());
        dp.setDailyGainLoss(s.getDailyGainLoss());
        dp.setDailyGainLossPercent(s.getDailyGainLossPercent());
        return dp;
    }
}
