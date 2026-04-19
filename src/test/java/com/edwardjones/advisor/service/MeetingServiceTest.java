package com.edwardjones.advisor.service;

import com.edwardjones.advisor.dto.MeetingRequest;
import com.edwardjones.advisor.dto.MeetingResponse;
import com.edwardjones.advisor.model.Meeting;
import com.edwardjones.advisor.repository.MeetingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MeetingService.
 * Story: MAP-22 — Meeting Scheduling
 * Covers all 3 Acceptance Criteria.
 */
class MeetingServiceTest {

    @Mock private MeetingRepository meetingRepository;
    @Mock private NotificationService notificationService;
    @InjectMocks private MeetingService meetingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * AC1: Given a logged-in client, When they book a meeting,
     * Then both client and advisor receive a calendar invite.
     */
    @Test
    @DisplayName("MAP-22-AC1: booking a meeting sends calendar invite to both parties")
    void bookMeeting_shouldSaveAndSendCalendarInvite() {
        MeetingRequest request = new MeetingRequest();
        request.setClientId(1L);
        request.setAdvisorId(2L);
        request.setScheduledAt(LocalDateTime.now().plusDays(3));

        Meeting saved = Meeting.builder()
                .id(10L).clientId(1L).advisorId(2L)
                .scheduledAt(request.getScheduledAt())
                .status(Meeting.MeetingStatus.SCHEDULED)
                .createdAt(LocalDateTime.now())
                .build();

        when(meetingRepository.save(any())).thenReturn(saved);

        MeetingResponse response = meetingService.bookMeeting(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getStatus()).isEqualTo(Meeting.MeetingStatus.SCHEDULED);
        verify(notificationService).sendCalendarInvite(1L, 2L, request.getScheduledAt());
    }

    /**
     * AC2: Given cancellation >= 24 hours in advance,
     * Then advisor is notified and slot is freed.
     */
    @Test
    @DisplayName("MAP-22-AC2: cancellation 24+ hours in advance notifies advisor")
    void cancelMeeting_withinPolicy_shouldSucceed() {
        Meeting existing = Meeting.builder()
                .id(10L).clientId(1L).advisorId(2L)
                .scheduledAt(LocalDateTime.now().plusDays(3))
                .status(Meeting.MeetingStatus.SCHEDULED)
                .build();

        when(meetingRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(meetingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MeetingResponse response = meetingService.cancelMeeting(10L, "Client request");

        assertThat(response.getStatus()).isEqualTo(Meeting.MeetingStatus.CANCELLED);
        verify(notificationService).sendCancellationNotice(eq(2L), any());
    }

    /**
     * AC3: Given cancellation < 24 hours in advance,
     * Then an error is thrown.
     */
    @Test
    @DisplayName("MAP-22-AC3: cancellation within 24 hours throws exception")
    void cancelMeeting_tooLate_shouldThrow() {
        Meeting existing = Meeting.builder()
                .id(10L).clientId(1L).advisorId(2L)
                .scheduledAt(LocalDateTime.now().plusHours(10))
                .status(Meeting.MeetingStatus.SCHEDULED)
                .build();

        when(meetingRepository.findById(10L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> meetingService.cancelMeeting(10L, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("24 hours");
    }

    /**
     * AC: Reschedule with valid notice notifies advisor of new slot.
     */
    @Test
    @DisplayName("MAP-22-AC: rescheduling notifies advisor of new slot")
    void rescheduleMeeting_withinPolicy_shouldUpdateAndNotify() {
        Meeting existing = Meeting.builder()
                .id(10L).clientId(1L).advisorId(2L)
                .scheduledAt(LocalDateTime.now().plusDays(3))
                .status(Meeting.MeetingStatus.SCHEDULED)
                .build();

        LocalDateTime newSlot = LocalDateTime.now().plusDays(5);
        when(meetingRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(meetingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MeetingResponse response = meetingService.rescheduleMeeting(10L, newSlot);

        assertThat(response.getStatus()).isEqualTo(Meeting.MeetingStatus.RESCHEDULED);
        assertThat(response.getScheduledAt()).isEqualTo(newSlot);
        verify(notificationService).sendRescheduleNotice(2L, newSlot);
    }
}
