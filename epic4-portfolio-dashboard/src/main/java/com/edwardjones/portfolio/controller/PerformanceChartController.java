package com.edwardjones.portfolio.controller;

import com.edwardjones.portfolio.dto.PerformanceChartResponse;
import com.edwardjones.portfolio.service.PerformanceChartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for portfolio performance chart data.
 * Story Reference: MAP-14 — Performance Charts over Time
 */
@RestController
@RequestMapping("/api/portfolio")
public class PerformanceChartController {

    private final PerformanceChartService chartService;

    public PerformanceChartController(PerformanceChartService chartService) {
        this.chartService = chartService;
    }

    /**
     * GET /api/portfolio/{clientId}/performance?range=ONE_MONTH
     * Returns performance chart data for the given time range.
     * Valid ranges: ONE_MONTH, THREE_MONTHS, SIX_MONTHS, ONE_YEAR, ALL
     *
     * @param clientId  the client's identifier
     * @param range     the time range (defaults to ONE_YEAR)
     * @return 200 OK with PerformanceChartResponse
     */
    @GetMapping("/{clientId}/performance")
    public ResponseEntity<PerformanceChartResponse> getPerformanceChart(
            @PathVariable String clientId,
            @RequestParam(defaultValue = "ONE_YEAR") String range) {

        PerformanceChartService.TimeRange timeRange;
        try {
            timeRange = PerformanceChartService.TimeRange.valueOf(range.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(chartService.getChartData(clientId, timeRange));
    }
}
