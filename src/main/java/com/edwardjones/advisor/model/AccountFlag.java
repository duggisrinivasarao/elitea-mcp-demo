package com.edwardjones.advisor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * AccountFlag entity — advisor-set priority flag on a client account.
 * Story: MAP-25 — Account Flagging
 */
@Entity
@Table(name = "account_flags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long clientId;

    @Column(nullable = false)
    private Long advisorId;

    private boolean flagged;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private LocalDateTime flaggedAt;

    private LocalDateTime resolvedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.flagged = false;
    }
}
