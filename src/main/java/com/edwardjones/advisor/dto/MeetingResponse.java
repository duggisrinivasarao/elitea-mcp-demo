package com.edwardjones.advisor.dto;

import com.edwardjones.advisor.model.Meeting;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO response for meeting operations.
 * Story: MAP-22
 */
@Data
@Builder
public class MeetingResponse {
    private Long id;
    private Long clientId;
    private Long advisorId;
    private LocalDateTime scheduledAt;
    private Meeting.MeetingStatus status;
    private LocalDateTime createdAt;
}
