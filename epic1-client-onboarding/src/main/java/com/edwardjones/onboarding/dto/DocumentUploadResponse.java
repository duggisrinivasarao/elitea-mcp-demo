package com.edwardjones.onboarding.dto;

import lombok.*;

/**
 * DTO for document upload response.
 * Story Reference: MAP-11 — Secure document upload & KYC
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentUploadResponse {
    private Long documentId;
    private String documentName;
    private String storagePath;
    private String verificationStatus;
    private String message;
}
