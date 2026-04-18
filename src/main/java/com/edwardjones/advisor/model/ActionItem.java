package com.edwardjones.advisor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ActionItem entity — individual task assigned from a meeting note.
 * Story: MAP-24 — Meeting Notes & Action Items
 */
@Entity
@Table(name = "action_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_note_id", nullable = false)
    private MeetingNote meetingNote;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionStatus status;

    private boolean reminderSent;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) this.status = ActionStatus.PENDING;
        this.reminderSent = false;
    }

    public enum ActionStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }
}
