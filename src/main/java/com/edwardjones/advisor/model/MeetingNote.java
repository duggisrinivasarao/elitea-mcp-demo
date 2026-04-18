package com.edwardjones.advisor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MeetingNote entity representing advisor-recorded notes and action items.
 * Story: MAP-24 — Meeting Notes & Action Items
 */
@Entity
@Table(name = "meeting_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long meetingId;

    @Column(nullable = false)
    private Long advisorId;

    @Column(nullable = false)
    private Long clientId;

    @Column(columnDefinition = "TEXT")
    private String noteContent;

    private boolean published;

    private LocalDateTime publishedAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "meetingNote")
    private List<ActionItem> actionItems;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.published = false;
    }
}
