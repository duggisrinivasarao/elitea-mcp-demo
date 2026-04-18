package com.edwardjones.advisor.controller;

import com.edwardjones.advisor.dto.AccountFlagRequest;
import com.edwardjones.advisor.model.AccountFlag;
import com.edwardjones.advisor.service.AccountFlagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for account flagging operations.
 * Story: MAP-25 — Account Flagging for Priority Review
 * Base URL: /api/account-flags
 */
@RestController
@RequestMapping("/api/account-flags")
@RequiredArgsConstructor
public class AccountFlagController {

    private final AccountFlagService flagService;

    /**
     * POST /api/account-flags
     * Flags a client account for priority review.
     */
    @PostMapping
    public ResponseEntity<AccountFlag> flagAccount(@Valid @RequestBody AccountFlagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flagService.flagAccount(request));
    }

    /**
     * DELETE /api/account-flags/{clientId}
     * Removes the priority flag from a client account.
     */
    @DeleteMapping("/{clientId}")
    public ResponseEntity<AccountFlag> unflagAccount(@PathVariable Long clientId) {
        return ResponseEntity.ok(flagService.unflagAccount(clientId));
    }

    /**
     * GET /api/account-flags/advisor/{advisorId}
     * Returns all accounts currently flagged by an advisor.
     */
    @GetMapping("/advisor/{advisorId}")
    public ResponseEntity<List<AccountFlag>> getFlaggedAccounts(@PathVariable Long advisorId) {
        return ResponseEntity.ok(flagService.getFlaggedAccounts(advisorId));
    }

    /**
     * GET /api/account-flags/{clientId}/status
     * Returns flag status for a specific client account.
     */
    @GetMapping("/{clientId}/status")
    public ResponseEntity<Boolean> isFlagged(@PathVariable Long clientId) {
        return ResponseEntity.ok(flagService.isAccountFlagged(clientId));
    }
}
