package com.edwardjones.advisor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for flagging or unflagging a client account.
 * Story: MAP-25
 */
@Data
public class AccountFlagRequest {

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Advisor ID is required")
    private Long advisorId;

    @NotBlank(message = "Reason for flagging is required")
    private String reason;
}
