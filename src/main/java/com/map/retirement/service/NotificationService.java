package com.map.retirement.service;

import com.map.retirement.model.ContributionTracker.AccountType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for sending retirement-related notifications.
 * Story: MAP-21 — Contribution Limit Notifications
 * Epic:  MAP-5  — Retirement Planning Tools
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Sends a contribution limit alert notification to the client.
     * Integration point — plug in email/push/SMS provider as needed.
     *
     * @param clientId    the client ID to notify
     * @param accountType the account type approaching the limit
     * @param percentage  current contribution percentage of annual limit
     */
    public void sendContributionLimitAlert(Long clientId, AccountType accountType,
                                           BigDecimal percentage) {
        log.info("[NOTIFICATION] Client {} — {} account has reached {}% of annual IRS limit. " +
                 "Encouraging contribution review.", clientId, accountType, percentage);
        // TODO: integrate with email/SMS notification provider
    }
}
