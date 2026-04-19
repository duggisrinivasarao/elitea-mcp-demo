package com.edwardjones.advisor.service;

import com.edwardjones.advisor.dto.MeetingRequest;
import com.edwardjones.advisor.dto.MeetingResponse;
import com.edwardjones.advisor.model.Meeting;
import com.edwardjones.advisor.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service handling meeting scheduling, cancellation, and rescheduling.
 * Story: MAP-22 — Meeting Scheduling
 */
@Service
@RequiredArgsConstructor
public class MeetingService {

    private static final int MIN_CANCEL_HOURS = 24;

    private final MeetingRepository meetingRepository;
    private final NotificationService notificationService;

    /**
     * Books a new meeting between client and advisor.
     * AC: Client views available slots and books an appointment.
     * AC: Both parties receive a calendar invite upon confirmation.
     *
     * @param request the meeting booking request
     * @return MeetingResponse with meeting details
     */
    @Transactional
    public MeetingResponse bookMeeting(MeetingRequest request) {
        Meeting meeting = Meeting.builder()
                .clientId(request.getClientId())
                .advisorId(request.getAdvisorId())
                .scheduledAt(request.getScheduledAt())
                .status(Meeting.MeetingStatus.SCHEDULED)
                .build();
        Meeting saved = meetingRepository.save(meeting);
        notificationService.sendCalendarInvite(saved.getClientId(), saved.getAdvisorId(), saved.getScheduledAt());
        return toResponse(saved);
    }

    /**
     * Cancels an existing meeting if at least 24 hours in advance.
     * AC: Cancellation notifies advisor and frees the slot if >= 24h in advance.
     *
     * @param meetingId the ID of the meeting to cancel
     * @param reason    optional cancellation reason
     * @return updated MeetingResponse
     */
    @Transactional
    public MeetingResponse cancelMeeting(Long meetingId, String reason) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found: " + meetingId));

        if (meeting.getScheduledAt().isBefore(LocalDateTime.now().plusHours(MIN_CANCEL_HOURS))) {
            throw new IllegalStateException("Cancellation must be at least 24 hours in advance.");
        }

        meeting.setStatus(Meeting.MeetingStatus.CANCELLED);
        meeting.setCancelReason(reason);
        meeting.setCancelledAt(LocalDateTime.now());
        Meeting saved = meetingRepository.save(meeting);
        notificationService.sendCancellationNotice(saved.getAdvisorId(), saved.getScheduledAt());
        return toResponse(saved);
    }

    /**
     * Reschedules an existing meeting to a new time slot.
     * AC: Advisor is notified and the original slot is freed on reschedule.
     *
     * @param meetingId   the ID of the meeting to reschedule
     * @param newSlot     the new scheduled time
     * @return updated MeetingResponse
     */
    @Transactional
    public MeetingResponse rescheduleMeeting(Long meetingId, LocalDateTime newSlot) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found: " + meetingId));

        if (meeting.getScheduledAt().isBefore(LocalDateTime.now().plusHours(MIN_CANCEL_HOURS))) {
            throw new IllegalStateException("Rescheduling must be at least 24 hours in advance.");
        }

        meeting.setScheduledAt(newSlot);
        meeting.setStatus(Meeting.MeetingStatus.RESCHEDULED);
        Meeting saved = meetingRepository.save(meeting);
        notificationService.sendRescheduleNotice(saved.getAdvisorId(), newSlot);
        return toResponse(saved);
    }

    /**
     * Retrieves scheduled meetings for a given advisor within a date range.
     *
     * @param advisorId the advisor's ID
     * @param from      start of range
     * @param to        end of range
     * @return list of MeetingResponses
     */
    public List<MeetingResponse> getAdvisorAvailability(Long advisorId, LocalDateTime from, LocalDateTime to) {
        return meetingRepository.findByAdvisorIdAndScheduledAtBetween(advisorId, from, to)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private MeetingResponse toResponse(Meeting m) {
        return MeetingResponse.builder()
                .id(m.getId())
                .clientId(m.getClientId())
                .advisorId(m.getAdvisorId())
                .scheduledAt(m.getScheduledAt())
                .status(m.getStatus())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
