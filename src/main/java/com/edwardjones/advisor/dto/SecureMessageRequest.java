package com.edwardjones.advisor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for sending a secure message.
 * Story: MAP-23
 */
@Data
public class SecureMessageRequest {

    @NotNull(message = "Sender ID is required")
    private Long senderId;

    @NotNull(message = "Recipient ID is required")
    private Long recipientId;

    @NotBlank(message = "Message content cannot be blank")
    private String content;
}
