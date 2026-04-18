package com.edwardjones.portfolio.controller;

import com.edwardjones.portfolio.dto.PortfolioDashboardResponse;
import com.edwardjones.portfolio.service.PortfolioDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for portfolio holdings dashboard.
 * Story Reference: MAP-13 — Real-time Portfolio Holdings Dashboard
 */
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioDashboardController {

    private final PortfolioDashboardService dashboardService;

    public PortfolioDashboardController(PortfolioDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * GET /api/portfolio/{clientId}/dashboard
     * Returns real-time holdings, total value, and daily gain/loss for a client.
     *
     * @param clientId the client's identifier
     * @return 200 OK with PortfolioDashboardResponse
     */
    @GetMapping("/{clientId}/dashboard")
    public ResponseEntity<PortfolioDashboardResponse> getDashboard(@PathVariable String clientId) {
        return ResponseEntity.ok(dashboardService.getDashboard(clientId));
    }
}
