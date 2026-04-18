package com.edwardjones.advisor.repository;

import com.edwardjones.advisor.model.MeetingNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for MeetingNote persistence.
 * Story: MAP-24
 */
@Repository
public interface MeetingNoteRepository extends JpaRepository<MeetingNote, Long> {

    List<MeetingNote> findByClientIdAndPublishedTrue(Long clientId);

    List<MeetingNote> findByMeetingIdAndPublishedTrue(Long meetingId);

    List<MeetingNote> findByAdvisorId(Long advisorId);
}
