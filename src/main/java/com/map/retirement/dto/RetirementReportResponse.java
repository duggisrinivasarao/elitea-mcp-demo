package com.map.retirement.dto;

import com.map.retirement.model.RetirementReport.ReportStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for retirement readiness report response.
 * Story: MAP-20 — Report Generation Output
 * Epic:  MAP-5  — Retirement Planning Tools
 */
public class RetirementReportResponse {

    private Long          id;
    private Long          advisorId;
    private Long          clientId;
    private ReportStatus  status;
    private String        reportUrl;
    private Boolean       hasIncompleteData;
    private List<String>  incompleteFields;
    private LocalDateTime generatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAdvisorId() { return advisorId; }
    public void setAdvisorId(Long advisorId) { this.advisorId = advisorId; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public String getReportUrl() { return reportUrl; }
    public void setReportUrl(String reportUrl) { this.reportUrl = reportUrl; }

    public Boolean getHasIncompleteData() { return hasIncompleteData; }
    public void setHasIncompleteData(Boolean hasIncompleteData) { this.hasIncompleteData = hasIncompleteData; }

    public List<String> getIncompleteFields() { return incompleteFields; }
    public void setIncompleteFields(List<String> incompleteFields) { this.incompleteFields = incompleteFields; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
