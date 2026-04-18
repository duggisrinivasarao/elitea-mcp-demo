package com.edwardjones.advisor.service;

import com.edwardjones.advisor.model.MeetingReminder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Notification service for all advisor collaboration communication events.
 * Handles emails, push notifications, calendar invites, and security alerts.
 */
@Service
@Slf4j
public class NotificationService {

    /**
     * Sends a calendar invite to both client and advisor after booking.
     * Story: MAP-22
     */
    public void sendCalendarInvite(Long clientId, Long advisorId, LocalDateTime scheduledAt) {
        log.info("[CALENDAR] Sending invite to client {} and advisor {} for {}", clientId, advisorId, scheduledAt);
    }

    /**
     * Notifies advisor of a meeting cancellation.
     * Story: MAP-22
     */
    public void sendCancellationNotice(Long advisorId, LocalDateTime cancelledSlot) {
        log.info("[CANCEL] Notifying advisor {} of cancellation for slot {}", advisorId, cancelledSlot);
    }

    /**
     * Notifies advisor of a meeting reschedule.
     * Story: MAP-22
     */
    public void sendRescheduleNotice(Long advisorId, LocalDateTime newSlot) {
        log.info("[RESCHEDULE] Notifying advisor {} of new slot {}", advisorId, newSlot);
    }

    /**
     * Notifies client of a new secure message.
     * Story: MAP-23
     */
    public void sendMessageNotification(Long recipientId, Long messageId) {
        log.info("[MSG] Notifying recipient {} of new message {}", recipientId, messageId);
    }

    /**
     * Alerts both parties on a security breach.
     * Story: MAP-23
     */
    public void sendSecurityBreachAlert(Long senderId, Long recipientId) {
        log.warn("[SECURITY] Breach alert sent to sender {} and recipient {}", senderId, recipientId);
    }

    /**
     * Notifies client when meeting notes are published.
     * Story: MAP-24
     */
    public void sendNotePublishedNotification(Long clientId, Long meetingId) {
        log.info("[NOTES] Notifying client {} that notes for meeting {} are available", clientId, meetingId);
    }

    /**
     * Sends an action item due reminder to a client.
     * Story: MAP-24
     */
    public void sendActionItemReminder(Long clientId, Long actionItemId) {
        log.info("[ACTION] Reminding client {} about action item {}", clientId, actionItemId);
    }

    /**
     * Sends a meeting reminder via specified channel.
     * Story: MAP-26
     */
    public void sendMeetingReminder(Long clientId, Long meetingId, MeetingReminder.ReminderChannel channel) {
        log.info("[REMINDER] Sending {} reminder to client {} for meeting {}", channel, clientId, meetingId);
    }
}
