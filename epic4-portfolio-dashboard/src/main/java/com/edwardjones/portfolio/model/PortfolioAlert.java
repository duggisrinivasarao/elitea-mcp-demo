package com.edwardjones.portfolio.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PortfolioAlert stores client-configured alert thresholds and notification preferences.
 * Story Reference: MAP-17 — Portfolio Alert Notifications
 */
@Entity
@Table(name = "portfolio_alerts")
public class PortfolioAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal thresholdPercent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    private boolean enabled = true;

    private LocalDateTime createdAt;

    private LocalDateTime lastTriggeredAt;

    public enum NotificationChannel {
        EMAIL, PUSH, BOTH
    }

    public PortfolioAlert() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public BigDecimal getThresholdPercent() { return thresholdPercent; }
    public void setThresholdPercent(BigDecimal thresholdPercent) { this.thresholdPercent = thresholdPercent; }
    public NotificationChannel getChannel() { return channel; }
    public void setChannel(NotificationChannel channel) { this.channel = channel; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getLastTriggeredAt() { return lastTriggeredAt; }
    public void setLastTriggeredAt(LocalDateTime lastTriggeredAt) { this.lastTriggeredAt = lastTriggeredAt; }
}
