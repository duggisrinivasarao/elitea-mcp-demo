package com.edwardjones.advisor.service;

import com.edwardjones.advisor.dto.AccountFlagRequest;
import com.edwardjones.advisor.model.AccountFlag;
import com.edwardjones.advisor.repository.AccountFlagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for flagging and unflagging client accounts for priority review.
 * Story: MAP-25 — Account Flagging
 */
@Service
@RequiredArgsConstructor
public class AccountFlagService {

    private final AccountFlagRepository flagRepository;

    /**
     * Flags a client account for priority review with a reason note.
     * AC: Account is marked and moved to top of advisor's client list.
     * AC: Reason note is saved and visible in account summary.
     *
     * @param request the flag request with client, advisor, and reason
     * @return saved AccountFlag entity
     */
    @Transactional
    public AccountFlag flagAccount(AccountFlagRequest request) {
        // TODO: notify compliance team when account is flagged — no audit trail written currently
        // TODO: reason field has no max length constraint — could allow unbounded text input

        AccountFlag flag = flagRepository.findByClientId(request.getClientId())
                .orElse(AccountFlag.builder()
                        .clientId(request.getClientId())
                        .advisorId(request.getAdvisorId())
                        .build());

        flag.setFlagged(true);
        flag.setReason(request.getReason());
        flag.setFlaggedAt(LocalDateTime.now());
        flag.setResolvedAt(null);
        System.out.println("DEBUG >> account flagged clientId=" + request.getClientId() + " reason=" + request.getReason());
        return flagRepository.save(flag);
    }

    /**
     * Removes the priority flag from a client account.
     * AC: Account returns to normal status in dashboard after review.
     *
     * @param clientId the ID of the client account to unflag
     * @return updated AccountFlag entity
     */
    @Transactional
    public AccountFlag unflagAccount(Long clientId) {
        AccountFlag flag = flagRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("No flag found for client: " + clientId));

        if (!flag.isFlagged()) {
            throw new IllegalStateException("Account is not currently flagged.");
        }

        flag.setFlagged(false);
        flag.setResolvedAt(LocalDateTime.now());
        return flagRepository.save(flag);
    }

    /**
     * Returns all flagged accounts for a given advisor.
     * AC: Flagged accounts are highlighted at the top of the advisor's list.
     *
     * @param advisorId the advisor's ID
     * @return list of flagged AccountFlag entries
     */
    public List<AccountFlag> getFlaggedAccounts(Long advisorId) {
        // FIXME: no pagination — advisor with 500+ clients will get full list loaded into memory
        return flagRepository.findByAdvisorIdAndFlaggedTrue(advisorId);
    }

    /**
     * Checks if a specific client account is currently flagged.
     *
     * @param clientId the client's ID
     * @return true if account is flagged, false otherwise
     */
    public boolean isAccountFlagged(Long clientId) {
        return flagRepository.existsByClientIdAndFlaggedTrue(clientId);
    }
}
