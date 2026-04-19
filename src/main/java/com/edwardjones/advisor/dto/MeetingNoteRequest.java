package com.edwardjones.advisor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for creating/publishing meeting notes.
 * Story: MAP-24
 */
@Data
public class MeetingNoteRequest {

    @NotNull(message = "Meeting ID is required")
    private Long meetingId;

    @NotNull(message = "Advisor ID is required")
    private Long advisorId;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotBlank(message = "Note content cannot be empty")
    private String noteContent;

    private List<ActionItemRequest> actionItems;

    @Data
    public static class ActionItemRequest {
        @NotBlank(message = "Action item description is required")
        private String description;

        @NotNull(message = "Due date is required")
        private LocalDate dueDate;
    }
}
