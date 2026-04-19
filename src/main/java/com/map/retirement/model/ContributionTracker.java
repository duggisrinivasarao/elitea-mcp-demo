package com.map.retirement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ContributionTracker Entity
 * Story: MAP-21 — IRA/401(k) Contribution Progress Tracker
 * Epic:  MAP-5  — Retirement Planning Tools
 */
@Entity
@Table(name = "contribution_trackers")
public class ContributionTracker {

    public enum AccountType {
        IRA, ROTH_IRA, FOUR_O_ONE_K, ROTH_401K
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @NotNull
    @Column(name = "tax_year", nullable = false)
    private Integer taxYear;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "ytd_contributions", nullable = false, precision = 15, scale = 2)
    private BigDecimal ytdContributions;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "annual_irs_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal annualIrsLimit;

    @Column(name = "contribution_percentage", precision = 5, scale = 2)
    private BigDecimal contributionPercentage;

    @Column(name = "limit_notification_sent")
    private Boolean limitNotificationSent = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

    public Integer getTaxYear() { return taxYear; }
    public void setTaxYear(Integer taxYear) { this.taxYear = taxYear; }

    public BigDecimal getYtdContributions() { return ytdContributions; }
    public void setYtdContributions(BigDecimal ytdContributions) { this.ytdContributions = ytdContributions; }

    public BigDecimal getAnnualIrsLimit() { return annualIrsLimit; }
    public void setAnnualIrsLimit(BigDecimal annualIrsLimit) { this.annualIrsLimit = annualIrsLimit; }

    public BigDecimal getContributionPercentage() { return contributionPercentage; }
    public void setContributionPercentage(BigDecimal contributionPercentage) { this.contributionPercentage = contributionPercentage; }

    public Boolean getLimitNotificationSent() { return limitNotificationSent; }
    public void setLimitNotificationSent(Boolean limitNotificationSent) { this.limitNotificationSent = limitNotificationSent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
