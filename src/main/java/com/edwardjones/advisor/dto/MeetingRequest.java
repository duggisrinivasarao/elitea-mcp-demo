package com.edwardjones.advisor.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO for booking a meeting.
 * Story: MAP-22
 */
@Data
public class MeetingRequest {

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Advisor ID is required")
    private Long advisorId;

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Meeting must be scheduled in the future")
    private LocalDateTime scheduledAt;
}
