package com.edwardjones.portfolio.service;

import com.edwardjones.portfolio.model.PortfolioAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Stub notification service for sending portfolio alert notifications.
 * In production, integrate with email/push providers (e.g., SendGrid, Firebase).
 * Story Reference: MAP-17 — Portfolio Alert Notifications
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final String PORTFOLIO_DEEP_LINK = "/portfolio/%s/holdings";

    /**
     * Sends a portfolio alert notification to a client via their configured channel.
     *
     * @param clientId     the client's identifier
     * @param channel      the notification channel (EMAIL, PUSH, BOTH)
     * @param changePct    the portfolio change percentage that triggered the alert
     */
    public void sendPortfolioAlert(String clientId, PortfolioAlert.NotificationChannel channel,
                                   BigDecimal changePct) {
        String deepLink = String.format(PORTFOLIO_DEEP_LINK, clientId);
        String message = String.format(
                "Your portfolio has changed by %.2f%%. View your holdings: %s", changePct, deepLink);

        switch (channel) {
            case EMAIL -> sendEmail(clientId, "Portfolio Alert", message);
            case PUSH  -> sendPush(clientId, message);
            case BOTH  -> {
                sendEmail(clientId, "Portfolio Alert", message);
                sendPush(clientId, message);
            }
        }
    }

    private void sendEmail(String clientId, String subject, String body) {
        // TODO: integrate with email provider (e.g., SendGrid)
        log.info("[EMAIL] To client={} Subject='{}' Body='{}'", clientId, subject, body);
    }

    private void sendPush(String clientId, String message) {
        // TODO: integrate with push provider (e.g., Firebase FCM)
        log.info("[PUSH] To client={} Message='{}'", clientId, message);
    }
}
