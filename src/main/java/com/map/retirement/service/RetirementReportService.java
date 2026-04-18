package com.map.retirement.service;

import com.map.retirement.dto.RetirementReportResponse;
import com.map.retirement.exception.ResourceNotFoundException;
import com.map.retirement.model.RetirementGoal;
import com.map.retirement.model.RetirementReport;
import com.map.retirement.model.RetirementReport.ReportStatus;
import com.map.retirement.model.RetirementScenario;
import com.map.retirement.repository.RetirementGoalRepository;
import com.map.retirement.repository.RetirementReportRepository;
import com.map.retirement.repository.RetirementScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for generating retirement readiness reports.
 * Story: MAP-20 — Retirement Readiness Report Generation
 * Epic:  MAP-5  — Retirement Planning Tools
 */
@Service
@Transactional
public class RetirementReportService {

    private final RetirementReportRepository   reportRepository;
    private final RetirementGoalRepository     goalRepository;
    private final RetirementScenarioRepository scenarioRepository;

    public RetirementReportService(RetirementReportRepository reportRepository,
                                   RetirementGoalRepository goalRepository,
                                   RetirementScenarioRepository scenarioRepository) {
        this.reportRepository  = reportRepository;
        this.goalRepository    = goalRepository;
        this.scenarioRepository = scenarioRepository;
    }

    /**
     * Generates a retirement readiness report for a client.
     * If client data is incomplete, marks the report and lists missing fields.
     *
     * @param advisorId the advisor requesting the report
     * @param clientId  the client for whom the report is generated
     * @return RetirementReportResponse with report details or incomplete data notice
     */
    public RetirementReportResponse generateReport(Long advisorId, Long clientId) {
        RetirementReport report = new RetirementReport();
        report.setAdvisorId(advisorId);
        report.setClientId(clientId);

        List<String> missingFields = detectMissingFields(clientId);

        if (!missingFields.isEmpty()) {
            report.setStatus(ReportStatus.GENERATED);
            report.setHasIncompleteData(true);
            report.setIncompleteFields(String.join(", ", missingFields));
        } else {
            report.setStatus(ReportStatus.GENERATED);
            report.setHasIncompleteData(false);
        }

        // Generate branded PDF URL (integration point)
        String reportUrl = generatePdfUrl(advisorId, clientId);
        report.setReportUrl(reportUrl);
        report.setGeneratedAt(LocalDateTime.now());

        RetirementReport saved = reportRepository.save(report);
        return mapToResponse(saved, missingFields);
    }

    /**
     * Retrieves all reports for a given client by advisor.
     *
     * @param advisorId the advisor ID
     * @param clientId  the client ID
     * @return list of RetirementReportResponse
     */
    @Transactional(readOnly = true)
    public List<RetirementReportResponse> getReportsByAdvisorAndClient(Long advisorId, Long clientId) {
        return reportRepository.findByAdvisorIdAndClientId(advisorId, clientId)
                .stream()
                .map(r -> mapToResponse(r, List.of()))
                .toList();
    }

    /**
     * Detects missing required data fields for a client's retirement report.
     *
     * @param clientId the client ID
     * @return list of missing field names
     */
    private List<String> detectMissingFields(Long clientId) {
        List<String> missing = new ArrayList<>();

        Optional<RetirementGoal> goal = goalRepository.findTopByClientIdOrderByCreatedAtDesc(clientId);
        if (goal.isEmpty()) {
            missing.add("Retirement Goal");
        } else {
            if (goal.get().getMonthlyIncomeNeeded() == null) missing.add("Monthly Income Needed");
            if (goal.get().getCurrentSavings() == null)      missing.add("Current Savings");
        }

        List<RetirementScenario> scenarios = scenarioRepository.findByClientId(clientId);
        if (scenarios.isEmpty()) {
            missing.add("Scenario Simulations");
        }

        return missing;
    }

    /**
     * Generates a branded PDF download URL.
     * Integration point — replace with actual PDF generation service.
     */
    private String generatePdfUrl(Long advisorId, Long clientId) {
        return "/api/v1/retirement/reports/download?advisorId=" + advisorId + "&clientId=" + clientId;
    }

    private RetirementReportResponse mapToResponse(RetirementReport r, List<String> missingFields) {
        RetirementReportResponse resp = new RetirementReportResponse();
        resp.setId(r.getId());
        resp.setAdvisorId(r.getAdvisorId());
        resp.setClientId(r.getClientId());
        resp.setStatus(r.getStatus());
        resp.setReportUrl(r.getReportUrl());
        resp.setHasIncompleteData(r.getHasIncompleteData());
        resp.setIncompleteFields(missingFields);
        resp.setGeneratedAt(r.getGeneratedAt());
        return resp;
    }
}
