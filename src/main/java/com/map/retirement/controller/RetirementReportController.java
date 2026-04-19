package com.map.retirement.controller;

import com.map.retirement.dto.RetirementReportResponse;
import com.map.retirement.service.RetirementReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for retirement readiness report generation.
 * Story: MAP-20 — Retirement Readiness Report Generation
 * Epic:  MAP-5  — Retirement Planning Tools
 *
 * Endpoints:
 *   POST /api/v1/advisors/{advisorId}/clients/{clientId}/retirement/reports
 *   GET  /api/v1/advisors/{advisorId}/clients/{clientId}/retirement/reports
 */
@RestController
@RequestMapping("/api/v1/advisors/{advisorId}/clients/{clientId}/retirement/reports")
public class RetirementReportController {

    private final RetirementReportService reportService;

    public RetirementReportController(RetirementReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Generates a retirement readiness report for a client.
     * Detects and marks incomplete data with field-level notices.
     *
     * @param advisorId the advisor ID
     * @param clientId  the client ID
     * @return generated report with PDF URL or incomplete data notice
     */
    @PostMapping
    public ResponseEntity<RetirementReportResponse> generateReport(
            @PathVariable Long advisorId,
            @PathVariable Long clientId) {
        RetirementReportResponse response = reportService.generateReport(advisorId, clientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all previously generated reports for a client by advisor.
     *
     * @param advisorId the advisor ID
     * @param clientId  the client ID
     * @return list of generated retirement reports
     */
    @GetMapping
    public ResponseEntity<List<RetirementReportResponse>> getReports(
            @PathVariable Long advisorId,
            @PathVariable Long clientId) {
        return ResponseEntity.ok(reportService.getReportsByAdvisorAndClient(advisorId, clientId));
    }
}
