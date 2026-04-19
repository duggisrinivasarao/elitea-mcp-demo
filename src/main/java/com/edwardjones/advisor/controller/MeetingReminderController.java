package com.edwardjones.advisor.controller;

import com.edwardjones.advisor.dto.ReminderPreferenceRequest;
import com.edwardjones.advisor.model.MeetingReminder;
import com.edwardjones.advisor.service.MeetingReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for meeting reminder preferences and scheduling.
 * Story: MAP-26 — Automated Meeting Reminders
 * Base URL: /api/reminders
 */
@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class MeetingReminderController {

    private final MeetingReminderService reminderService;

    /**
     * POST /api/reminders/schedule
     * Schedules a reminder for an upcoming meeting.
     */
    @PostMapping("/schedule")
    public ResponseEntity<MeetingReminder> scheduleReminder(
            @RequestParam Long meetingId,
            @RequestParam Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime meetingTime) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reminderService.scheduleReminder(meetingId, clientId, meetingTime));
    }

    /**
     * POST /api/reminders/opt-out
     * Opts a client out of all automated reminders.
     */
    @PostMapping("/opt-out")
    public ResponseEntity<Void> optOut(@Valid @RequestBody ReminderPreferenceRequest request) {
        if (request.isOptOut()) {
            reminderService.optOutReminders(request.getClientId());
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/reminders/meeting/{meetingId}
     * Returns all reminders associated with a specific meeting.
     */
    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<List<MeetingReminder>> getRemindersForMeeting(@PathVariable Long meetingId) {
        return ResponseEntity.ok(reminderService.getRemindersForMeeting(meetingId));
    }
}
