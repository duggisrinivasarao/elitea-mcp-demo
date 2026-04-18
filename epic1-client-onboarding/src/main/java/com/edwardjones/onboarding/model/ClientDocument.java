package com.edwardjones.onboarding.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a KYC document uploaded by a client.
 * Story Reference: MAP-11 — Secure document upload & KYC
 */
@Entity
@Table(name = "client_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @NotBlank
    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @NotBlank
    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "mime_type")
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    /**
     * Types of KYC documents accepted by Edward Jones.
     */
    public enum DocumentType {
        GOVERNMENT_ID,
        PASSPORT,
        DRIVERS_LICENSE,
        PROOF_OF_ADDRESS,
        TAX_RETURN,
        BANK_STATEMENT,
        OTHER
    }

    /**
     * Verification status of the submitted document.
     */
    public enum VerificationStatus {
        PENDING,
        APPROVED,
        REJECTED,
        REQUIRES_RESUBMISSION
    }
}
