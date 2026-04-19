package com.edwardjones.advisor.repository;

import com.edwardjones.advisor.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Meeting persistence.
 * Story: MAP-22
 */
@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByClientIdAndStatus(Long clientId, Meeting.MeetingStatus status);

    List<Meeting> findByAdvisorIdAndStatus(Long advisorId, Meeting.MeetingStatus status);

    List<Meeting> findByAdvisorIdAndScheduledAtBetween(Long advisorId, LocalDateTime from, LocalDateTime to);

    List<Meeting> findByReminderSentFalseAndScheduledAtBetween(LocalDateTime from, LocalDateTime to);
}
