package com.edwardjones.portfolio.controller;

import com.edwardjones.portfolio.dto.AdvisorDashboardResponse;
import com.edwardjones.portfolio.service.AdvisorDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for the advisor's consolidated portfolio dashboard.
 * Story Reference: MAP-16 — Advisor Consolidated Dashboard
 */
@RestController
@RequestMapping("/api/advisor")
public class AdvisorDashboardController {

    private final AdvisorDashboardService advisorDashboardService;

    public AdvisorDashboardController(AdvisorDashboardService advisorDashboardService) {
        this.advisorDashboardService = advisorDashboardService;
    }

    /**
     * GET /api/advisor/{advisorId}/dashboard?sortBy=performance
     * Returns consolidated portfolio summary for all assigned clients.
     * Flagged accounts (>=5% daily change) are surfaced at the top.
     *
     * @param advisorId the advisor's identifier
     * @param clientIds list of client IDs assigned to this advisor
     * @param sortBy    sort field: "performance" or "value" (default: "value")
     * @return 200 OK with AdvisorDashboardResponse
     */
    @GetMapping("/{advisorId}/dashboard")
    public ResponseEntity<AdvisorDashboardResponse> getAdvisorDashboard(
            @PathVariable String advisorId,
            @RequestParam List<String> clientIds,
            @RequestParam(defaultValue = "value") String sortBy) {

        return ResponseEntity.ok(advisorDashboardService.getDashboard(advisorId, clientIds, sortBy));
    }
}
