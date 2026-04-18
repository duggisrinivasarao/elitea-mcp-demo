package com.edwardjones.advisor.service;

import com.edwardjones.advisor.model.MeetingReminder;
import com.edwardjones.advisor.repository.MeetingReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for scheduling and dispatching automated meeting reminders.
 * Story: MAP-26 — Automated Meeting Reminders
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingReminderService {

    private static final long REMINDER_HOURS_BEFORE = 48L;

    private final MeetingReminderRepository reminderRepository;
    private final NotificationService notificationService;

    /**
     * Schedules a reminder for an upcoming meeting.
     * AC: 48-hour reminder is sent via email and push notification.
     *
     * @param meetingId   the ID of the meeting
     * @param clientId    the client's ID
     * @param meetingTime the time of the meeting
     * @return saved MeetingReminder entity
     */
    @Transactional
    public MeetingReminder scheduleReminder(Long meetingId, Long clientId, LocalDateTime meetingTime) {
        boolean optedOut = reminderRepository.existsByClientIdAndOptedOutTrue(clientId);

        MeetingReminder reminder = MeetingReminder.builder()
                .meetingId(meetingId)
                .clientId(clientId)
                .channel(MeetingReminder.ReminderChannel.BOTH)
                .scheduledFor(meetingTime.minusHours(REMINDER_HOURS_BEFORE))
                .status(optedOut
                        ? MeetingReminder.ReminderStatus.SKIPPED_OPT_OUT
                        : MeetingReminder.ReminderStatus.PENDING)
                .optedOut(optedOut)
                .build();

        return reminderRepository.save(reminder);
    }

    /**
     * Opt a client out of automated reminders.
     * AC: No reminders sent if client opts out.
     *
     * @param clientId the client's ID
     */
    @Transactional
    public void optOutReminders(Long clientId) {
        List<MeetingReminder> pending = reminderRepository.findByClientIdAndOptedOutFalse(clientId);
        pending.forEach(r -> {
            r.setOptedOut(true);
            r.setStatus(MeetingReminder.ReminderStatus.SKIPPED_OPT_OUT);
        });
        reminderRepository.saveAll(pending);
        log.info("Client {} opted out of all reminders.", clientId);
    }

    /**
     * Scheduled job: dispatches pending reminders whose scheduled time has passed.
     * AC: Email and push notifications sent 48 hours before the meeting.
     * Runs every hour.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void dispatchPendingReminders() {
        List<MeetingReminder> due = reminderRepository.findByStatusAndScheduledForBefore(
                MeetingReminder.ReminderStatus.PENDING, LocalDateTime.now());

        due.forEach(r -> {
            try {
                notificationService.sendMeetingReminder(r.getClientId(), r.getMeetingId(), r.getChannel());
                r.setStatus(MeetingReminder.ReminderStatus.SENT);
                r.setSentAt(LocalDateTime.now());
            } catch (Exception ex) {
                r.setStatus(MeetingReminder.ReminderStatus.FAILED);
                log.error("Failed to send reminder {} for client {}", r.getId(), r.getClientId(), ex);
            }
            reminderRepository.save(r);
        });
    }

    /**
     * Retrieves all reminders for a given meeting.
     *
     * @param meetingId the meeting ID
     * @return list of MeetingReminders
     */
    public List<MeetingReminder> getRemindersForMeeting(Long meetingId) {
        return reminderRepository.findByMeetingId(meetingId);
    }
}
