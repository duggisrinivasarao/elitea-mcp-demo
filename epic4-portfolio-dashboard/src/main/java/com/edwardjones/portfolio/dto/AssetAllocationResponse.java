package com.edwardjones.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for asset allocation breakdown with actual vs target comparison.
 * Story Reference: MAP-15 — Asset Allocation Breakdown
 */
public class AssetAllocationResponse {

    private String clientId;
    private List<AllocationSegment> segments;
    private boolean targetAllocationSet;

    public static class AllocationSegment {
        private String assetType;
        private BigDecimal actualPercent;
        private BigDecimal actualValue;
        private BigDecimal targetPercent;
        private List<HoldingSummary> holdings;

        public static class HoldingSummary {
            private String ticker;
            private String name;
            private BigDecimal value;
            private BigDecimal percent;

            public String getTicker() { return ticker; }
            public void setTicker(String ticker) { this.ticker = ticker; }
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public BigDecimal getValue() { return value; }
            public void setValue(BigDecimal value) { this.value = value; }
            public BigDecimal getPercent() { return percent; }
            public void setPercent(BigDecimal percent) { this.percent = percent; }
        }

        public String getAssetType() { return assetType; }
        public void setAssetType(String assetType) { this.assetType = assetType; }
        public BigDecimal getActualPercent() { return actualPercent; }
        public void setActualPercent(BigDecimal actualPercent) { this.actualPercent = actualPercent; }
        public BigDecimal getActualValue() { return actualValue; }
        public void setActualValue(BigDecimal actualValue) { this.actualValue = actualValue; }
        public BigDecimal getTargetPercent() { return targetPercent; }
        public void setTargetPercent(BigDecimal targetPercent) { this.targetPercent = targetPercent; }
        public List<HoldingSummary> getHoldings() { return holdings; }
        public void setHoldings(List<HoldingSummary> holdings) { this.holdings = holdings; }
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public List<AllocationSegment> getSegments() { return segments; }
    public void setSegments(List<AllocationSegment> segments) { this.segments = segments; }
    public boolean isTargetAllocationSet() { return targetAllocationSet; }
    public void setTargetAllocationSet(boolean targetAllocationSet) { this.targetAllocationSet = targetAllocationSet; }
}
