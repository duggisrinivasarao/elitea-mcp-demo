package com.edwardjones.advisor.dto;

import com.edwardjones.advisor.model.SecureMessage;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO response for secure message operations.
 * Story: MAP-23
 */
@Data
@Builder
public class SecureMessageResponse {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private boolean readByRecipient;
    private LocalDateTime readAt;
    private SecureMessage.MessageStatus status;
    private LocalDateTime sentAt;
}
