package com.edwardjones.advisor.controller;

import com.edwardjones.advisor.dto.MeetingRequest;
import com.edwardjones.advisor.dto.MeetingResponse;
import com.edwardjones.advisor.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for meeting scheduling operations.
 * Story: MAP-22 — Meeting Scheduling
 * Base URL: /api/meetings
 */
@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    /**
     * POST /api/meetings
     * Books a new meeting between client and advisor.
     */
    @PostMapping
    public ResponseEntity<MeetingResponse> bookMeeting(@Valid @RequestBody MeetingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(meetingService.bookMeeting(request));
    }

    /**
     * DELETE /api/meetings/{meetingId}
     * Cancels an existing meeting (min 24 hours notice required).
     */
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<MeetingResponse> cancelMeeting(
            @PathVariable Long meetingId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(meetingService.cancelMeeting(meetingId, reason));
    }

    /**
     * PATCH /api/meetings/{meetingId}/reschedule
     * Reschedules a meeting to a new time slot.
     */
    @PatchMapping("/{meetingId}/reschedule")
    public ResponseEntity<MeetingResponse> rescheduleMeeting(
            @PathVariable Long meetingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newSlot) {
        return ResponseEntity.ok(meetingService.rescheduleMeeting(meetingId, newSlot));
    }

    /**
     * GET /api/meetings/advisor/{advisorId}/availability
     * Returns advisor availability within a date range.
     */
    @GetMapping("/advisor/{advisorId}/availability")
    public ResponseEntity<List<MeetingResponse>> getAdvisorAvailability(
            @PathVariable Long advisorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(meetingService.getAdvisorAvailability(advisorId, from, to));
    }
}
