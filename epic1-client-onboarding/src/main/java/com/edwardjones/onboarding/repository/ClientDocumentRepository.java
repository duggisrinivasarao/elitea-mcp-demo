package com.edwardjones.onboarding.repository;

import com.edwardjones.onboarding.model.ClientDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ClientDocument entity data access.
 * Story Reference: MAP-11 — Secure document upload & KYC
 */
@Repository
public interface ClientDocumentRepository extends JpaRepository<ClientDocument, Long> {

    /**
     * Finds all documents uploaded by a specific client.
     * @param clientId the client's ID
     * @return list of documents
     */
    List<ClientDocument> findByClientId(Long clientId);

    /**
     * Finds documents by client and verification status.
     * @param clientId the client's ID
     * @param status verification status
     * @return list of matching documents
     */
    List<ClientDocument> findByClientIdAndVerificationStatus(
            Long clientId,
            ClientDocument.VerificationStatus status
    );

    /**
     * Checks whether a client has uploaded all required KYC documents.
     * @param clientId the client's ID
     * @param type the document type
     * @return true if at least one approved document of the type exists
     */
    boolean existsByClientIdAndDocumentTypeAndVerificationStatus(
            Long clientId,
            ClientDocument.DocumentType type,
            ClientDocument.VerificationStatus status
    );
}
