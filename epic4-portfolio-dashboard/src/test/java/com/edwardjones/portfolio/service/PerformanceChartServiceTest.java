package com.edwardjones.portfolio.service;

import com.edwardjones.portfolio.dto.PerformanceChartResponse;
import com.edwardjones.portfolio.model.PerformanceSnapshot;
import com.edwardjones.portfolio.repository.PerformanceSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PerformanceChartService.
 * Story Reference: MAP-14 — Performance Charts over Time
 */
class PerformanceChartServiceTest {

    @Mock
    private PerformanceSnapshotRepository snapshotRepository;

    @InjectMocks
    private PerformanceChartService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    // AC: Chart updates to reflect the selected period
    @Test
    void getChartData_shouldReturnDataPointsForOneMonth() {
        List<PerformanceSnapshot> snapshots = buildSnapshots("CLIENT1", 30);
        when(snapshotRepository.findByClientIdAndSnapshotDateBetweenOrderBySnapshotDateAsc(
                eq("CLIENT1"), any(), any())).thenReturn(snapshots);

        PerformanceChartResponse response = service.getChartData("CLIENT1",
                PerformanceChartService.TimeRange.ONE_MONTH);

        assertThat(response.getDataPoints()).hasSize(30);
        assertThat(response.getTimeRange()).isEqualTo("ONE_MONTH");
    }

    // AC: Exact portfolio value and date are available per data point (tooltip support)
    @Test
    void getChartData_dataPointsShouldContainDateAndValue() {
        List<PerformanceSnapshot> snapshots = buildSnapshots("CLIENT1", 5);
        when(snapshotRepository.findByClientIdAndSnapshotDateBetweenOrderBySnapshotDateAsc(
                eq("CLIENT1"), any(), any())).thenReturn(snapshots);

        PerformanceChartResponse response = service.getChartData("CLIENT1",
                PerformanceChartService.TimeRange.THREE_MONTHS);

        response.getDataPoints().forEach(dp -> {
            assertThat(dp.getDate()).isNotNull();
            assertThat(dp.getTotalValue()).isNotNull();
        });
    }

    // AC: If insufficient data, only available data is shown with informational message
    @Test
    void getChartData_shouldFlagInsufficientDataWithMessage() {
        // Only 2 snapshots when 365 expected → insufficient
        List<PerformanceSnapshot> sparse = buildSnapshots("CLIENT1", 2);
        when(snapshotRepository.findByClientIdAndSnapshotDateBetweenOrderBySnapshotDateAsc(
                eq("CLIENT1"), any(), any())).thenReturn(sparse);

        PerformanceChartResponse response = service.getChartData("CLIENT1",
                PerformanceChartService.TimeRange.ONE_YEAR);

        assertThat(response.isInsufficientData()).isTrue();
        assertThat(response.getInformationalMessage()).isNotBlank();
        assertThat(response.getDataPoints()).hasSize(2); // shows what's available
    }

    // AC: ALL range returns full history
    @Test
    void getChartData_allRangeReturnsFullHistory() {
        List<PerformanceSnapshot> full = buildSnapshots("CLIENT1", 500);
        when(snapshotRepository.findByClientIdOrderBySnapshotDateAsc("CLIENT1")).thenReturn(full);

        PerformanceChartResponse response = service.getChartData("CLIENT1",
                PerformanceChartService.TimeRange.ALL);

        assertThat(response.getDataPoints()).hasSize(500);
        assertThat(response.isInsufficientData()).isFalse();
    }

    // AC: Blank clientId throws
    @Test
    void getChartData_shouldThrowForBlankClientId() {
        assertThatThrownBy(() -> service.getChartData("  ",
                PerformanceChartService.TimeRange.ONE_MONTH))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private List<PerformanceSnapshot> buildSnapshots(String clientId, int count) {
        List<PerformanceSnapshot> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            PerformanceSnapshot s = new PerformanceSnapshot();
            s.setClientId(clientId);
            s.setSnapshotDate(LocalDate.now().minusDays(count - i));
            s.setTotalValue(new BigDecimal("100000").add(new BigDecimal(i * 100)));
            s.setDailyGainLoss(new BigDecimal("50.00"));
            s.setDailyGainLossPercent(new BigDecimal("0.05"));
            list.add(s);
        }
        return list;
    }
}
