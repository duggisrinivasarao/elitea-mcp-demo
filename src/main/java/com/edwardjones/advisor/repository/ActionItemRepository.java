package com.edwardjones.advisor.repository;

import com.edwardjones.advisor.model.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository for ActionItem persistence.
 * Story: MAP-24
 */
@Repository
public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

    List<ActionItem> findByMeetingNoteIdAndStatus(Long meetingNoteId, ActionItem.ActionStatus status);

    @Query("SELECT a FROM ActionItem a WHERE a.dueDate <= :deadline AND a.reminderSent = false AND a.status = 'PENDING'")
    List<ActionItem> findDueItemsWithoutReminder(LocalDate deadline);
}
