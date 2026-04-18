package com.edwardjones.portfolio.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PerformanceSnapshot captures daily portfolio value for historical chart rendering.
 * Story Reference: MAP-14 — Performance Charts over Time
 */
@Entity
@Table(name = "performance_snapshots", indexes = {
    @Index(name = "idx_perf_client_date", columnList = "clientId, snapshotDate")
})
public class PerformanceSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private LocalDate snapshotDate;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal totalValue;

    @Column(precision = 15, scale = 4)
    private BigDecimal dailyGainLoss;

    @Column(precision = 7, scale = 4)
    private BigDecimal dailyGainLossPercent;

    public PerformanceSnapshot() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public LocalDate getSnapshotDate() { return snapshotDate; }
    public void setSnapshotDate(LocalDate snapshotDate) { this.snapshotDate = snapshotDate; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public BigDecimal getDailyGainLoss() { return dailyGainLoss; }
    public void setDailyGainLoss(BigDecimal dailyGainLoss) { this.dailyGainLoss = dailyGainLoss; }
    public BigDecimal getDailyGainLossPercent() { return dailyGainLossPercent; }
    public void setDailyGainLossPercent(BigDecimal dailyGainLossPercent) { this.dailyGainLossPercent = dailyGainLossPercent; }
}
