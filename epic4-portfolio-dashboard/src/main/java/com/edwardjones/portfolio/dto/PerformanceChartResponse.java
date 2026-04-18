package com.edwardjones.portfolio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for portfolio performance chart data.
 * Story Reference: MAP-14 — Performance Charts over Time
 */
public class PerformanceChartResponse {

    private String clientId;
    private String timeRange;
    private List<DataPoint> dataPoints;
    private boolean insufficientData;
    private String informationalMessage;

    public static class DataPoint {
        private LocalDate date;
        private BigDecimal totalValue;
        private BigDecimal dailyGainLoss;
        private BigDecimal dailyGainLossPercent;

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public BigDecimal getTotalValue() { return totalValue; }
        public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
        public BigDecimal getDailyGainLoss() { return dailyGainLoss; }
        public void setDailyGainLoss(BigDecimal dailyGainLoss) { this.dailyGainLoss = dailyGainLoss; }
        public BigDecimal getDailyGainLossPercent() { return dailyGainLossPercent; }
        public void setDailyGainLossPercent(BigDecimal v) { this.dailyGainLossPercent = v; }
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getTimeRange() { return timeRange; }
    public void setTimeRange(String timeRange) { this.timeRange = timeRange; }
    public List<DataPoint> getDataPoints() { return dataPoints; }
    public void setDataPoints(List<DataPoint> dataPoints) { this.dataPoints = dataPoints; }
    public boolean isInsufficientData() { return insufficientData; }
    public void setInsufficientData(boolean insufficientData) { this.insufficientData = insufficientData; }
    public String getInformationalMessage() { return informationalMessage; }
    public void setInformationalMessage(String informationalMessage) { this.informationalMessage = informationalMessage; }
}
