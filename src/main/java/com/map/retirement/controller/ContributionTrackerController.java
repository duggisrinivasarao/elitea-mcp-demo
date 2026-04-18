package com.map.retirement.controller;

import com.map.retirement.dto.ContributionTrackerRequest;
import com.map.retirement.dto.ContributionTrackerResponse;
import com.map.retirement.model.ContributionTracker.AccountType;
import com.map.retirement.service.ContributionTrackerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for IRA/401(k) contribution tracking.
 * Story: MAP-21 — IRA/401(k) Contribution Progress Tracker
 * Epic:  MAP-5  — Retirement Planning Tools
 *
 * Endpoints:
 *   GET    /api/v1/clients/{clientId}/retirement/contributions
 *   PUT    /api/v1/clients/{clientId}/retirement/contributions
 *   PATCH  /api/v1/retirement/contributions/irs-limits
 */
@RestController
@RequestMapping("/api/v1")
public class ContributionTrackerController {

    private final ContributionTrackerService trackerService;

    public ContributionTrackerController(ContributionTrackerService trackerService) {
        this.trackerService = trackerService;
    }

    /**
     * Retrieves all contribution trackers for a client for the current tax year.
     *
     * @param clientId the client ID
     * @return list of contribution trackers with progress percentages
     */
    @GetMapping("/clients/{clientId}/retirement/contributions")
    public ResponseEntity<List<ContributionTrackerResponse>> getContributions(
            @PathVariable Long clientId) {
        return ResponseEntity.ok(trackerService.getTrackersByClient(clientId));
    }

    /**
     * Updates year-to-date contributions for a client account.
     *
     * @param clientId the client ID
     * @param request  contribution update with account type and YTD amount
     * @return updated tracker with recalculated percentage
     */
    @PutMapping("/clients/{clientId}/retirement/contributions")
    public ResponseEntity<ContributionTrackerResponse> updateContribution(
            @PathVariable Long clientId,
            @Valid @RequestBody ContributionTrackerRequest request) {
        return ResponseEntity.ok(trackerService.updateContribution(clientId, request));
    }

    /**
     * Updates IRS annual limits for a given account type and tax year.
     * Called at the start of each new year when IRS publishes updated limits.
     *
     * @param accountType account type (IRA, ROTH_IRA, FOUR_O_ONE_K, ROTH_401K)
     * @param newLimit    new IRS contribution limit
     * @param taxYear     the tax year to apply the update
     * @return number of records updated
     */
    @PatchMapping("/retirement/contributions/irs-limits")
    public ResponseEntity<String> updateIrsLimits(
            @RequestParam AccountType accountType,
            @RequestParam BigDecimal newLimit,
            @RequestParam Integer taxYear) {
        int updated = trackerService.updateAnnualIrsLimits(accountType, newLimit, taxYear);
        return ResponseEntity.ok(updated + " contribution tracker(s) updated for " + taxYear);
    }
}
