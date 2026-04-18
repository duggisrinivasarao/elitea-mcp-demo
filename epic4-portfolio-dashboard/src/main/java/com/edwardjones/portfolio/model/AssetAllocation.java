package com.edwardjones.portfolio.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AssetAllocation represents the target and actual allocation settings for a client.
 * Story Reference: MAP-15 — Asset Allocation Breakdown
 */
@Entity
@Table(name = "asset_allocations")
public class AssetAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String clientId;

    // Target allocations (percentages)
    @Column(precision = 5, scale = 2)
    private BigDecimal targetStockPercent;

    @Column(precision = 5, scale = 2)
    private BigDecimal targetBondPercent;

    @Column(precision = 5, scale = 2)
    private BigDecimal targetMutualFundPercent;

    @Column(precision = 5, scale = 2)
    private BigDecimal targetEtfPercent;

    @Column(precision = 5, scale = 2)
    private BigDecimal targetCashPercent;

    private LocalDateTime lastUpdated;

    public AssetAllocation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public BigDecimal getTargetStockPercent() { return targetStockPercent; }
    public void setTargetStockPercent(BigDecimal targetStockPercent) { this.targetStockPercent = targetStockPercent; }
    public BigDecimal getTargetBondPercent() { return targetBondPercent; }
    public void setTargetBondPercent(BigDecimal targetBondPercent) { this.targetBondPercent = targetBondPercent; }
    public BigDecimal getTargetMutualFundPercent() { return targetMutualFundPercent; }
    public void setTargetMutualFundPercent(BigDecimal v) { this.targetMutualFundPercent = v; }
    public BigDecimal getTargetEtfPercent() { return targetEtfPercent; }
    public void setTargetEtfPercent(BigDecimal targetEtfPercent) { this.targetEtfPercent = targetEtfPercent; }
    public BigDecimal getTargetCashPercent() { return targetCashPercent; }
    public void setTargetCashPercent(BigDecimal targetCashPercent) { this.targetCashPercent = targetCashPercent; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
