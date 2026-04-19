package com.edwardjones.advisor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * SecureMessage entity for encrypted advisor-client communication.
 * Story: MAP-23 — Secure Messaging
 */
@Entity
@Table(name = "secure_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecureMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long recipientId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedContent;

    @Column(nullable = false)
    private boolean readByRecipient;

    private LocalDateTime readAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status;

    @Column(updatable = false)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
        if (this.status == null) this.status = MessageStatus.DELIVERED;
        this.readByRecipient = false;
    }

    public enum MessageStatus {
        DELIVERED, READ, FLAGGED_BREACH, TERMINATED
    }
}
