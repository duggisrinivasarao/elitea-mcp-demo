package com.edwardjones.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for the advisor's consolidated portfolio dashboard.
 * Story Reference: MAP-16 — Advisor Consolidated Dashboard
 */
public class AdvisorDashboardResponse {

    private String advisorId;
    private int totalClients;
    private BigDecimal totalAUM;
    private List<ClientPortfolioSummary> clients;

    public static class ClientPortfolioSummary {
        private String clientId;
        private String clientName;
        private BigDecimal portfolioValue;
        private BigDecimal dailyChangePercent;
        private String riskProfile;
        private boolean flagged;
        private String flagReason;

        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
        public String getClientName() { return clientName; }
        public void setClientName(String clientName) { this.clientName = clientName; }
        public BigDecimal getPortfolioValue() { return portfolioValue; }
        public void setPortfolioValue(BigDecimal portfolioValue) { this.portfolioValue = portfolioValue; }
        public BigDecimal getDailyChangePercent() { return dailyChangePercent; }
        public void setDailyChangePercent(BigDecimal dailyChangePercent) { this.dailyChangePercent = dailyChangePercent; }
        public String getRiskProfile() { return riskProfile; }
        public void setRiskProfile(String riskProfile) { this.riskProfile = riskProfile; }
        public boolean isFlagged() { return flagged; }
        public void setFlagged(boolean flagged) { this.flagged = flagged; }
        public String getFlagReason() { return flagReason; }
        public void setFlagReason(String flagReason) { this.flagReason = flagReason; }
    }

    public String getAdvisorId() { return advisorId; }
    public void setAdvisorId(String advisorId) { this.advisorId = advisorId; }
    public int getTotalClients() { return totalClients; }
    public void setTotalClients(int totalClients) { this.totalClients = totalClients; }
    public BigDecimal getTotalAUM() { return totalAUM; }
    public void setTotalAUM(BigDecimal totalAUM) { this.totalAUM = totalAUM; }
    public List<ClientPortfolioSummary> getClients() { return clients; }
    public void setClients(List<ClientPortfolioSummary> clients) { this.clients = clients; }
}
