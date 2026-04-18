package com.edwardjones.portfolio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for the portfolio holdings dashboard.
 * Story Reference: MAP-13 — Real-time Portfolio Holdings Dashboard
 */
public class PortfolioDashboardResponse {

    private String clientId;
    private BigDecimal totalValue;
    private BigDecimal dailyGainLoss;
    private BigDecimal dailyGainLossPercent;
    private List<HoldingDto> holdings;
    private LocalDateTime lastUpdated;
    private boolean dataStale;
    private String staleDataMessage;

    public static class HoldingDto {
        private Long id;
        private String ticker;
        private String name;
        private String assetType;
        private BigDecimal quantity;
        private BigDecimal currentPrice;
        private BigDecimal currentValue;
        private BigDecimal dailyChange;
        private BigDecimal dailyChangePercent;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTicker() { return ticker; }
        public void setTicker(String ticker) { this.ticker = ticker; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAssetType() { return assetType; }
        public void setAssetType(String assetType) { this.assetType = assetType; }
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
        public BigDecimal getCurrentPrice() { return currentPrice; }
        public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
        public BigDecimal getCurrentValue() { return currentValue; }
        public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }
        public BigDecimal getDailyChange() { return dailyChange; }
        public void setDailyChange(BigDecimal dailyChange) { this.dailyChange = dailyChange; }
        public BigDecimal getDailyChangePercent() { return dailyChangePercent; }
        public void setDailyChangePercent(BigDecimal dailyChangePercent) { this.dailyChangePercent = dailyChangePercent; }
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public BigDecimal getDailyGainLoss() { return dailyGainLoss; }
    public void setDailyGainLoss(BigDecimal dailyGainLoss) { this.dailyGainLoss = dailyGainLoss; }
    public BigDecimal getDailyGainLossPercent() { return dailyGainLossPercent; }
    public void setDailyGainLossPercent(BigDecimal dailyGainLossPercent) { this.dailyGainLossPercent = dailyGainLossPercent; }
    public List<HoldingDto> getHoldings() { return holdings; }
    public void setHoldings(List<HoldingDto> holdings) { this.holdings = holdings; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    public boolean isDataStale() { return dataStale; }
    public void setDataStale(boolean dataStale) { this.dataStale = dataStale; }
    public String getStaleDataMessage() { return staleDataMessage; }
    public void setStaleDataMessage(String staleDataMessage) { this.staleDataMessage = staleDataMessage; }
}
