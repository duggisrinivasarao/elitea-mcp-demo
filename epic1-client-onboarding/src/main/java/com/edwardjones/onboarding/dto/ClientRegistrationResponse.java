package com.edwardjones.onboarding.dto;

import lombok.*;

/**
 * DTO for client registration response.
 * Story Reference: MAP-8 — Register and complete financial profile
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRegistrationResponse {
    private Long clientId;
    private String fullName;
    private String email;
    private String status;
    private String message;
}
