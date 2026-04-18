package com.edwardjones.portfolio.controller;

import com.edwardjones.portfolio.dto.PortfolioAlertRequest;
import com.edwardjones.portfolio.model.PortfolioAlert;
import com.edwardjones.portfolio.service.PortfolioAlertService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing portfolio alert configurations.
 * Story Reference: MAP-17 — Portfolio Alert Notifications
 */
@RestController
@RequestMapping("/api/portfolio/{clientId}/alerts")
public class PortfolioAlertController {

    private final PortfolioAlertService alertService;

    public PortfolioAlertController(PortfolioAlertService alertService) {
        this.alertService = alertService;
    }

    /**
     * POST /api/portfolio/{clientId}/alerts
     * Configures a new alert threshold for a client.
     */
    @PostMapping
    public ResponseEntity<PortfolioAlert> configureAlert(
            @PathVariable String clientId,
            @Valid @RequestBody PortfolioAlertRequest request) {
        return ResponseEntity.ok(alertService.configureAlert(clientId, request));
    }

    /**
     * GET /api/portfolio/{clientId}/alerts
     * Returns all configured alerts for a client.
     */
    @GetMapping
    public ResponseEntity<List<PortfolioAlert>> getAlerts(@PathVariable String clientId) {
        return ResponseEntity.ok(alertService.getAlerts(clientId));
    }

    /**
     * PUT /api/portfolio/{clientId}/alerts/disable
     * Disables all alerts for a client.
     */
    @PutMapping("/disable")
    public ResponseEntity<Void> disableAlerts(@PathVariable String clientId) {
        alertService.disableAlerts(clientId);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/portfolio/{clientId}/alerts/enable
     * Re-enables all alerts for a client.
     */
    @PutMapping("/enable")
    public ResponseEntity<Void> enableAlerts(@PathVariable String clientId) {
        alertService.enableAlerts(clientId);
        return ResponseEntity.noContent().build();
    }
}
