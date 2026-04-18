package com.edwardjones.advisor.repository;

import com.edwardjones.advisor.model.MeetingReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for MeetingReminder persistence.
 * Story: MAP-26
 */
@Repository
public interface MeetingReminderRepository extends JpaRepository<MeetingReminder, Long> {

    List<MeetingReminder> findByMeetingId(Long meetingId);

    List<MeetingReminder> findByClientIdAndOptedOutFalse(Long clientId);

    List<MeetingReminder> findByStatusAndScheduledForBefore(
            MeetingReminder.ReminderStatus status, LocalDateTime threshold);

    boolean existsByClientIdAndOptedOutTrue(Long clientId);
}
