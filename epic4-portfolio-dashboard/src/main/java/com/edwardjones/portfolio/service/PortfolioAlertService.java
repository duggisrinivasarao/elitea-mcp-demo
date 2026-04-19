package com.edwardjones.portfolio.service;

import com.edwardjones.portfolio.dto.PortfolioAlertRequest;
import com.edwardjones.portfolio.model.PortfolioAlert;
import com.edwardjones.portfolio.repository.PortfolioAlertRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing portfolio alert thresholds and triggering notifications.
 * Story Reference: MAP-17 — Portfolio Alert Notifications
 */
@Service
public class PortfolioAlertService {

    private final PortfolioAlertRepository alertRepository;
    private final NotificationService notificationService;

    public PortfolioAlertService(PortfolioAlertRepository alertRepository,
                                  NotificationService notificationService) {
        this.alertRepository = alertRepository;
        this.notificationService = notificationService;
    }

    /**
     * Creates or updates a portfolio alert threshold for a client.
     * AC: Client configures threshold → notification sent when breached.
     *
     * @param clientId the client's identifier
     * @param request  the alert configuration request
     * @return the saved PortfolioAlert
     */
    public PortfolioAlert configureAlert(String clientId, PortfolioAlertRequest request) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client ID must not be blank");
        }

        PortfolioAlert alert = new PortfolioAlert();
        alert.setClientId(clientId);
        alert.setThresholdPercent(request.getThresholdPercent());
        alert.setChannel(PortfolioAlert.NotificationChannel.valueOf(request.getChannel().toUpperCase()));
        alert.setEnabled(true);
        alert.setCreatedAt(LocalDateTime.now());
        return alertRepository.save(alert);
    }

    /**
     * Disables all portfolio alerts for a client.
     * AC: When client updates preferences to disable, no further alerts are sent.
     *
     * @param clientId the client's identifier
     */
    public void disableAlerts(String clientId) {
        List<PortfolioAlert> alerts = alertRepository.findByClientId(clientId);
        alerts.forEach(a -> a.setEnabled(false));
        alertRepository.saveAll(alerts);
    }

    /**
     * Re-enables all portfolio alerts for a client.
     *
     * @param clientId the client's identifier
     */
    public void enableAlerts(String clientId) {
        List<PortfolioAlert> alerts = alertRepository.findByClientId(clientId);
        alerts.forEach(a -> a.setEnabled(true));
        alertRepository.saveAll(alerts);
    }

    /**
     * Evaluates current portfolio change against active thresholds and fires notifications.
     * AC: When portfolio value changes by threshold %, notification is sent via preferred channel.
     * AC: Client clicking notification deep-links to relevant portfolio section.
     *
     * @param clientId         the client's identifier
     * @param currentChangePct the current daily change percentage
     */
    public void evaluateAndNotify(String clientId, BigDecimal currentChangePct) {
        List<PortfolioAlert> activeAlerts = alertRepository.findByClientIdAndEnabledTrue(clientId);
        for (PortfolioAlert alert : activeAlerts) {
            if (currentChangePct.abs().compareTo(alert.getThresholdPercent()) >= 0) {
                notificationService.sendPortfolioAlert(clientId, alert.getChannel(), currentChangePct);
                alert.setLastTriggeredAt(LocalDateTime.now());
                alertRepository.save(alert);
            }
        }
    }

    /**
     * Returns all configured alerts for a client.
     *
     * @param clientId the client's identifier
     * @return list of PortfolioAlert
     */
    public List<PortfolioAlert> getAlerts(String clientId) {
        return alertRepository.findByClientId(clientId);
    }
}
