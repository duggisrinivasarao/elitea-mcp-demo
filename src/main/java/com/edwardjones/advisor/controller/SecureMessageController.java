package com.edwardjones.advisor.controller;

import com.edwardjones.advisor.dto.SecureMessageRequest;
import com.edwardjones.advisor.dto.SecureMessageResponse;
import com.edwardjones.advisor.service.SecureMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for secure advisor-client messaging.
 * Story: MAP-23 — Secure Messaging
 * Base URL: /api/messages
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class SecureMessageController {

    private final SecureMessageService messageService;

    /**
     * POST /api/messages
     * Sends a secure encrypted message to a client.
     */
    @PostMapping
    public ResponseEntity<SecureMessageResponse> sendMessage(@Valid @RequestBody SecureMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.sendMessage(request));
    }

    /**
     * PATCH /api/messages/{messageId}/read
     * Marks a message as read and records a read receipt.
     */
    @PatchMapping("/{messageId}/read")
    public ResponseEntity<SecureMessageResponse> markAsRead(@PathVariable Long messageId) {
        return ResponseEntity.ok(messageService.markAsRead(messageId));
    }

    /**
     * POST /api/messages/{messageId}/breach
     * Flags a security breach and terminates the session.
     */
    @PostMapping("/{messageId}/breach")
    public ResponseEntity<Void> reportBreach(@PathVariable Long messageId) {
        messageService.flagSecurityBreach(messageId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/messages/unread?recipientId={id}
     * Returns all unread messages for a recipient.
     */
    @GetMapping("/unread")
    public ResponseEntity<List<SecureMessageResponse>> getUnread(@RequestParam Long recipientId) {
        return ResponseEntity.ok(messageService.getUnreadMessages(recipientId));
    }
}
