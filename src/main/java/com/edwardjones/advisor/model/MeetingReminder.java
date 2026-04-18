package com.edwardjones.advisor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * MeetingReminder entity — automated reminder record for upcoming meetings.
 * Story: MAP-26 — Automated Meeting Reminders
 */
@Entity
@Table(name = "meeting_reminders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long meetingId;

    @Column(nullable = false)
    private Long clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderStatus status;

    private LocalDateTime scheduledFor;

    private LocalDateTime sentAt;

    private boolean optedOut;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = ReminderStatus.PENDING;
        this.optedOut = false;
    }

    public enum ReminderChannel {
        EMAIL, PUSH, BOTH
    }

    public enum ReminderStatus {
        PENDING, SENT, FAILED, SKIPPED_OPT_OUT
    }
}
