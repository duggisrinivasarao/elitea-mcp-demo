package com.edwardjones.advisor.service;

import com.edwardjones.advisor.model.MeetingReminder;
import com.edwardjones.advisor.repository.MeetingReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MeetingReminderService.
 * Story: MAP-26 — Automated Meeting Reminders
 * Covers all 3 Acceptance Criteria.
 */
class MeetingReminderServiceTest {

    @Mock private MeetingReminderRepository reminderRepository;
    @Mock private NotificationService notificationService;
    @InjectMocks private MeetingReminderService reminderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * AC1: Given a meeting is scheduled 48h away,
     * Then client receives email and push notification reminder.
     */
    @Test
    @DisplayName("MAP-26-AC1: reminder scheduled 48 hours before meeting")
    void scheduleReminder_shouldSetScheduledFor48HoursBefore() {
        LocalDateTime meetingTime = LocalDateTime.now().plusDays(3);
        when(reminderRepository.existsByClientIdAndOptedOutTrue(1L)).thenReturn(false);
        when(reminderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MeetingReminder result = reminderService.scheduleReminder(10L, 1L, meetingTime);

        assertThat(result.getScheduledFor()).isEqualTo(meetingTime.minusHours(48));
        assertThat(result.getStatus()).isEqualTo(MeetingReminder.ReminderStatus.PENDING);
        assertThat(result.getChannel()).isEqualTo(MeetingReminder.ReminderChannel.BOTH);
    }

    /**
     * AC2: Given pending reminders exist,
     * When dispatch runs, Then notifications are sent and status updated to SENT.
     */
    @Test
    @DisplayName("MAP-26-AC2: dispatching pending reminders sends notifications and marks SENT")
    void dispatchPendingReminders_shouldSendAndMarkSent() {
        MeetingReminder reminder = MeetingReminder.builder()
                .id(1L).meetingId(10L).clientId(1L)
                .channel(MeetingReminder.ReminderChannel.BOTH)
                .status(MeetingReminder.ReminderStatus.PENDING)
                .scheduledFor(LocalDateTime.now().minusMinutes(5))
                .build();

        when(reminderRepository.findByStatusAndScheduledForBefore(
                eq(MeetingReminder.ReminderStatus.PENDING), any())).thenReturn(List.of(reminder));

        reminderService.dispatchPendingReminders();

        verify(notificationService).sendMeetingReminder(1L, 10L, MeetingReminder.ReminderChannel.BOTH);
        assertThat(reminder.getStatus()).isEqualTo(MeetingReminder.ReminderStatus.SENT);
        assertThat(reminder.getSentAt()).isNotNull();
    }

    /**
     * AC3: Given client opts out,
     * Then no reminders are sent for that client.
     */
    @Test
    @DisplayName("MAP-26-AC3: opted-out client gets SKIPPED_OPT_OUT reminder status")
    void scheduleReminder_optedOutClient_shouldSkip() {
        LocalDateTime meetingTime = LocalDateTime.now().plusDays(3);
        when(reminderRepository.existsByClientIdAndOptedOutTrue(1L)).thenReturn(true);
        when(reminderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MeetingReminder result = reminderService.scheduleReminder(10L, 1L, meetingTime);

        assertThat(result.getStatus()).isEqualTo(MeetingReminder.ReminderStatus.SKIPPED_OPT_OUT);
        assertThat(result.isOptedOut()).isTrue();
    }

    /**
     * Edge: Opt-out updates all pending reminders to SKIPPED_OPT_OUT.
     */
    @Test
    @DisplayName("MAP-26-edge: opting out cancels all pending reminders")
    void optOutReminders_shouldCancelAllPendingReminders() {
        MeetingReminder reminder = MeetingReminder.builder()
                .id(1L).clientId(1L).meetingId(10L)
                .status(MeetingReminder.ReminderStatus.PENDING)
                .optedOut(false).build();

        when(reminderRepository.findByClientIdAndOptedOutFalse(1L)).thenReturn(List.of(reminder));

        reminderService.optOutReminders(1L);

        assertThat(reminder.isOptedOut()).isTrue();
        assertThat(reminder.getStatus()).isEqualTo(MeetingReminder.ReminderStatus.SKIPPED_OPT_OUT);
        verify(reminderRepository).saveAll(anyList());
    }

    /**
     * Edge: Dispatch failure marks reminder as FAILED without crashing.
     */
    @Test
    @DisplayName("MAP-26-edge: dispatch failure marks reminder as FAILED")
    void dispatchPendingReminders_onFailure_shouldMarkFailed() {
        MeetingReminder reminder = MeetingReminder.builder()
                .id(1L).meetingId(10L).clientId(1L)
                .channel(MeetingReminder.ReminderChannel.EMAIL)
                .status(MeetingReminder.ReminderStatus.PENDING)
                .scheduledFor(LocalDateTime.now().minusMinutes(5))
                .build();

        when(reminderRepository.findByStatusAndScheduledForBefore(
                eq(MeetingReminder.ReminderStatus.PENDING), any())).thenReturn(List.of(reminder));
        doThrow(new RuntimeException("SMTP error"))
                .when(notificationService).sendMeetingReminder(anyLong(), anyLong(), any());

        reminderService.dispatchPendingReminders();

        assertThat(reminder.getStatus()).isEqualTo(MeetingReminder.ReminderStatus.FAILED);
    }
}
