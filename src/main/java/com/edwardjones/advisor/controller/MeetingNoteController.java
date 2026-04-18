package com.edwardjones.advisor.controller;

import com.edwardjones.advisor.dto.MeetingNoteRequest;
import com.edwardjones.advisor.model.ActionItem;
import com.edwardjones.advisor.model.MeetingNote;
import com.edwardjones.advisor.service.MeetingNoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for meeting notes and action items.
 * Story: MAP-24 — Meeting Notes & Action Items
 * Base URL: /api/meeting-notes
 */
@RestController
@RequestMapping("/api/meeting-notes")
@RequiredArgsConstructor
public class MeetingNoteController {

    private final MeetingNoteService noteService;

    /**
     * POST /api/meeting-notes
     * Creates and publishes meeting notes with action items.
     */
    @PostMapping
    public ResponseEntity<MeetingNote> publishNotes(@Valid @RequestBody MeetingNoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.publishNotes(request));
    }

    /**
     * GET /api/meeting-notes/client/{clientId}
     * Returns all published notes accessible by a client.
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<MeetingNote>> getClientNotes(@PathVariable Long clientId) {
        return ResponseEntity.ok(noteService.getClientNotes(clientId));
    }

    /**
     * PATCH /api/meeting-notes/action-items/{actionItemId}/status
     * Updates the status of an action item.
     */
    @PatchMapping("/action-items/{actionItemId}/status")
    public ResponseEntity<ActionItem> updateActionItemStatus(
            @PathVariable Long actionItemId,
            @RequestParam ActionItem.ActionStatus status) {
        return ResponseEntity.ok(noteService.updateActionItemStatus(actionItemId, status));
    }
}
