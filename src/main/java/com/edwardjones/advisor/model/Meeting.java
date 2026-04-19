package com.edwardjones.advisor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Meeting entity representing a scheduled advisor-client appointment.
 * Story: MAP-22 — Meeting Scheduling
 */
@Entity
@Table(name = "meetings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long advisorId;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingStatus status;

    private String cancelReason;

    private LocalDateTime cancelledAt;

    private boolean reminderSent;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = MeetingStatus.SCHEDULED;
    }

    public enum MeetingStatus {
        SCHEDULED, CONFIRMED, CANCELLED, RESCHEDULED, COMPLETED
    }
}
