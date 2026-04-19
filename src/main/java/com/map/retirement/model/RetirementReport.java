package com.map.retirement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * RetirementReport Entity
 * Story: MAP-20 — Retirement Readiness Report Generation
 * Epic:  MAP-5  — Retirement Planning Tools
 */
@Entity
@Table(name = "retirement_reports")
public class RetirementReport {

    public enum ReportStatus {
        PENDING, GENERATED, FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "advisor_id", nullable = false)
    private Long advisorId;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "report_url")
    private String reportUrl;

    @Column(name = "has_incomplete_data")
    private Boolean hasIncompleteData = false;

    @Column(name = "incomplete_fields", columnDefinition = "TEXT")
    private String incompleteFields;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

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

    public String getIncompleteFields() { return incompleteFields; }
    public void setIncompleteFields(String incompleteFields) { this.incompleteFields = incompleteFields; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
