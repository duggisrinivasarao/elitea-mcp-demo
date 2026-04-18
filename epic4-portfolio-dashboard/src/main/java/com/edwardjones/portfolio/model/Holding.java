package com.edwardjones.portfolio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Holding entity representing a single investment position in a client's portfolio.
 * Story Reference: MAP-13 — Real-time Portfolio Holdings Dashboard
 */
@Entity
@Table(name = "holdings")
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String clientId;

    @NotBlank
    @Column(nullable = false)
    private String ticker;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType assetType;

    @PositiveOrZero
    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal currentPrice;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal currentValue;

    @Column(precision = 15, scale = 4)
    private BigDecimal dailyChange;

    @Column(precision = 7, scale = 4)
    private BigDecimal dailyChangePercent;

    @Column(precision = 15, scale = 4)
    private BigDecimal costBasis;

    private LocalDateTime lastUpdated;

    private boolean dataStale = false;

    public enum AssetType {
        STOCK, BOND, MUTUAL_FUND, ETF, CASH
    }

    public Holding() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public AssetType getAssetType() { return assetType; }
    public void setAssetType(AssetType assetType) { this.assetType = assetType; }
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
    public BigDecimal getCostBasis() { return costBasis; }
    public void setCostBasis(BigDecimal costBasis) { this.costBasis = costBasis; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    public boolean isDataStale() { return dataStale; }
    public void setDataStale(boolean dataStale) { this.dataStale = dataStale; }
}
