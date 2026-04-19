package com.map.retirement.dto;

import com.map.retirement.model.ContributionTracker.AccountType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for updating contribution tracker.
 * Story: MAP-21 — Contribution Tracker Input
 * Epic:  MAP-5  — Retirement Planning Tools
 */
public class ContributionTrackerRequest {

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Year-to-date contributions amount is required")
    @DecimalMin(value = "0.0", message = "YTD contributions cannot be negative")
    private BigDecimal ytdContributions;

    // Getters and Setters
    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

    public BigDecimal getYtdContributions() { return ytdContributions; }
    public void setYtdContributions(BigDecimal ytdContributions) { this.ytdContributions = ytdContributions; }
}
