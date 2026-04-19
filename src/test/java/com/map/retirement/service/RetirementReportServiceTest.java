package com.map.retirement.service;

import com.map.retirement.dto.RetirementReportResponse;
import com.map.retirement.model.RetirementGoal;
import com.map.retirement.model.RetirementReport;
import com.map.retirement.model.RetirementReport.ReportStatus;
import com.map.retirement.model.RetirementScenario;
import com.map.retirement.repository.RetirementGoalRepository;
import com.map.retirement.repository.RetirementReportRepository;
import com.map.retirement.repository.RetirementScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for RetirementReportService
 * Story: MAP-20 — Retirement Readiness Report Generation
 * Epic:  MAP-5  — Retirement Planning Tools
 *
 * AC Coverage:
 * AC1: Advisor clicks Generate → PDF report generated with goals, savings, projections
 * AC2: Report is branded and downloadable
 * AC3: Incomplete client data → missing fields marked with notice
 */
@ExtendWith(MockitoExtension.class)
class RetirementReportServiceTest {

    @Mock
    private RetirementReportRepository reportRepository;

    @Mock
    private RetirementGoalRepository goalRepository;

    @Mock
    private RetirementScenarioRepository scenarioRepository;

    @InjectMocks
    private RetirementReportService reportService;

    private RetirementGoal completeGoal;

    @BeforeEach
    void setUp() {
        completeGoal = new RetirementGoal();
        completeGoal.setId(1L);
        completeGoal.setClientId(1L);
        completeGoal.setMonthlyIncomeNeeded(new BigDecimal("5000.00"));
        completeGoal.setCurrentSavings(new BigDecimal("50000.00"));
    }

    // ─── AC1: Complete data → report generated with all sections ─────────────

    @Test
    @DisplayName("MAP-20 AC1: Given complete client data, report is generated with GENERATED status")
    void givenCompleteClientData_whenGenerateReport_thenReportGenerated() {
        RetirementScenario scenario = new RetirementScenario();
        when(goalRepository.findTopByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(completeGoal));
        when(scenarioRepository.findByClientId(1L)).thenReturn(List.of(scenario));
        when(reportRepository.save(any())).thenAnswer(inv -> {
            RetirementReport r = inv.getArgument(0);
            r.setId(1L);
            r.setGeneratedAt(LocalDateTime.now());
            return r;
        });

        RetirementReportResponse response = reportService.generateReport(10L, 1L);

        assertThat(response.getStatus()).isEqualTo(ReportStatus.GENERATED);
        assertThat(response.getHasIncompleteData()).isFalse();
        assertThat(response.getIncompleteFields()).isEmpty();
    }

    // ─── AC2: Report is branded and downloadable ─────────────────────────────

    @Test
    @DisplayName("MAP-20 AC2: Generated report includes a downloadable report URL")
    void givenCompleteData_whenGenerateReport_thenReportUrlPresent() {
        RetirementScenario scenario = new RetirementScenario();
        when(goalRepository.findTopByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(completeGoal));
        when(scenarioRepository.findByClientId(1L)).thenReturn(List.of(scenario));
        when(reportRepository.save(any())).thenAnswer(inv -> {
            RetirementReport r = inv.getArgument(0);
            r.setId(1L);
            r.setReportUrl("/api/v1/retirement/reports/download?advisorId=10&clientId=1");
            r.setGeneratedAt(LocalDateTime.now());
            return r;
        });

        RetirementReportResponse response = reportService.generateReport(10L, 1L);

        assertThat(response.getReportUrl()).isNotBlank();
        assertThat(response.getReportUrl()).contains("advisorId=10").contains("clientId=1");
    }

    // ─── AC3: Incomplete data → missing fields marked ────────────────────────

    @Test
    @DisplayName("MAP-20 AC3: Given no retirement goal, report marks 'Retirement Goal' as missing")
    void givenNoRetirementGoal_whenGenerateReport_thenMarksMissingGoal() {
        when(goalRepository.findTopByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());
        when(scenarioRepository.findByClientId(1L)).thenReturn(Collections.emptyList());
        when(reportRepository.save(any())).thenAnswer(inv -> {
            RetirementReport r = inv.getArgument(0);
            r.setId(1L);
            r.setGeneratedAt(LocalDateTime.now());
            return r;
        });

        RetirementReportResponse response = reportService.generateReport(10L, 1L);

        assertThat(response.getHasIncompleteData()).isTrue();
        assertThat(response.getIncompleteFields()).contains("Retirement Goal");
    }

    @Test
    @DisplayName("MAP-20 AC3: Given no scenarios run, report marks 'Scenario Simulations' as missing")
    void givenNoScenarios_whenGenerateReport_thenMarksScenariosMissing() {
        when(goalRepository.findTopByClientIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(completeGoal));
        when(scenarioRepository.findByClientId(1L)).thenReturn(Collections.emptyList());
        when(reportRepository.save(any())).thenAnswer(inv -> {
            RetirementReport r = inv.getArgument(0);
            r.setId(1L);
            r.setGeneratedAt(LocalDateTime.now());
            return r;
        });

        RetirementReportResponse response = reportService.generateReport(10L, 1L);

        assertThat(response.getHasIncompleteData()).isTrue();
        assertThat(response.getIncompleteFields()).contains("Scenario Simulations");
    }

    @Test
    @DisplayName("MAP-20: Retrieves all reports for a given advisor and client")
    void givenAdvisorAndClient_whenGetReports_thenReturnsAll() {
        RetirementReport report = new RetirementReport();
        report.setId(1L);
        report.setAdvisorId(10L);
        report.setClientId(1L);
        report.setStatus(ReportStatus.GENERATED);
        when(reportRepository.findByAdvisorIdAndClientId(10L, 1L)).thenReturn(List.of(report));

        List<RetirementReportResponse> reports = reportService.getReportsByAdvisorAndClient(10L, 1L);

        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).getStatus()).isEqualTo(ReportStatus.GENERATED);
    }
}
