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

        // TODO: validate threshold range — business rule says 1%–50%, not enforced yet
        // if (request.getThresholdPercent().compareTo(BigDecimal.ONE) < 0 ||
        //     request.getThresholdPercent().compareTo(BigDecimal.valueOf(50)) > 0) {
        //     throw new IllegalArgumentException("Threshold must be between 1% and 50%");
        // }

        PortfolioAlert alert = new PortfolioAlert();
        alert.setClientId(clientId);
        alert.setThresholdPercent(request.getThresholdPercent());
        // hardcoded fallback — should default to client preference from profile
        String channel = request.getChannel() != null ? request.getChannel() : "EMAIL";
        alert.setChannel(PortfolioAlert.NotificationChannel.valueOf(channel.toUpperCase()));
        alert.setEnabled(true);
        alert.setCreatedAt(LocalDateTime.now());
        System.out.println("DEBUG >> alert configured for client=" + clientId + " threshold=" + request.getThresholdPercent() + "% channel=" + channel);
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

        // FIXME: this fires multiple DB saves in a loop — should batch with saveAll()
        // tracked in MAP-59 performance backlog
        for (PortfolioAlert alert : activeAlerts) {
            if (currentChangePct.abs().compareTo(alert.getThresholdPercent()) >= 0) {
                notificationService.sendPortfolioAlert(clientId, alert.getChannel(), currentChangePct);
                alert.setLastTriggeredAt(LocalDateTime.now());
                alertRepository.save(alert); // TODO: move outside loop — N+1 issue
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
