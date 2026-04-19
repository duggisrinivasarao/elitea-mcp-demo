package com.map.retirement.dto;

import com.map.retirement.model.ContributionTracker.AccountType;
import java.math.BigDecimal;

/**
 * DTO for contribution tracker response.
 * Story: MAP-21 — Contribution Tracker Output
 * Epic:  MAP-5  — Retirement Planning Tools
 */
public class ContributionTrackerResponse {

    private Long        id;
    private Long        clientId;
    private AccountType accountType;
    private Integer     taxYear;
    private BigDecimal  ytdContributions;
    private BigDecimal  annualIrsLimit;
    private BigDecimal  contributionPercentage;
    private Boolean     limitNotificationSent;

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
}
