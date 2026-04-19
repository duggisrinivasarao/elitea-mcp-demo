package com.edwardjones.portfolio.controller;

import com.edwardjones.portfolio.dto.AssetAllocationResponse;
import com.edwardjones.portfolio.service.AssetAllocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for asset allocation breakdown.
 * Story Reference: MAP-15 — Asset Allocation Breakdown
 */
@RestController
@RequestMapping("/api/portfolio")
public class AssetAllocationController {

    private final AssetAllocationService allocationService;

    public AssetAllocationController(AssetAllocationService allocationService) {
        this.allocationService = allocationService;
    }

    /**
     * GET /api/portfolio/{clientId}/allocation
     * Returns asset allocation with actual vs. target breakdown per segment.
     *
     * @param clientId the client's identifier
     * @return 200 OK with AssetAllocationResponse
     */
    @GetMapping("/{clientId}/allocation")
    public ResponseEntity<AssetAllocationResponse> getAllocation(@PathVariable String clientId) {
        return ResponseEntity.ok(allocationService.getAllocation(clientId));
    }
}
