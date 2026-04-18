package com.edwardjones.portfolio.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for configuring a portfolio alert threshold.
 * Story Reference: MAP-17 — Portfolio Alert Notifications
 */
public class PortfolioAlertRequest {

    @NotNull(message = "Threshold percent is required")
    @DecimalMin(value = "0.1", message = "Threshold must be at least 0.1%")
    @DecimalMax(value = "100.0", message = "Threshold cannot exceed 100%")
    private java.math.BigDecimal thresholdPercent;

    @NotNull(message = "Notification channel is required")
    private String channel;

    public java.math.BigDecimal getThresholdPercent() { return thresholdPercent; }
    public void setThresholdPercent(java.math.BigDecimal thresholdPercent) { this.thresholdPercent = thresholdPercent; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}
