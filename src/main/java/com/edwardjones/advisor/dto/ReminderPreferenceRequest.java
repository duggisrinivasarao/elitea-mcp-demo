package com.edwardjones.advisor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for updating reminder opt-out preferences.
 * Story: MAP-26
 */
@Data
public class ReminderPreferenceRequest {

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Opt-out preference is required")
    private boolean optOut;
}
