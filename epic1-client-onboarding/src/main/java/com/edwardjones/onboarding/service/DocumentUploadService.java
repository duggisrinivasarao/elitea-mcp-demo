package com.edwardjones.onboarding.service;

import com.edwardjones.onboarding.dto.DocumentUploadResponse;
import com.edwardjones.onboarding.model.Client;
import com.edwardjones.onboarding.model.ClientDocument;
import com.edwardjones.onboarding.repository.ClientDocumentRepository;
import com.edwardjones.onboarding.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Service for handling secure KYC document uploads.
 * Story Reference: MAP-11 — Secure document upload & KYC
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentUploadService {

    private final ClientDocumentRepository documentRepository;
    private final ClientRepository clientRepository;

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "application/pdf", "image/jpeg", "image/png"
    );

    /**
     * Uploads a KYC document for a client securely.
     *
     * @param clientId     the client's ID
     * @param file         the multipart file being uploaded
     * @param documentType the type of document being uploaded
     * @return DocumentUploadResponse with storage path and status
     * @throws IllegalArgumentException if file type or size is invalid
     */
    @Transactional
    public DocumentUploadResponse uploadDocument(
            Long clientId,
            MultipartFile file,
            ClientDocument.DocumentType documentType) {

        log.info("Uploading {} document for client ID: {}", documentType, clientId);

        validateFile(file);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Client not found with ID: " + clientId
                ));

        String storagePath = generateStoragePath(clientId, file.getOriginalFilename());

        ClientDocument document = ClientDocument.builder()
                .client(client)
                .documentName(file.getOriginalFilename())
                .documentType(documentType)
                .storagePath(storagePath)
                .fileSizeBytes(file.getSize())
                .mimeType(file.getContentType())
                .verificationStatus(ClientDocument.VerificationStatus.PENDING)
                .build();

        ClientDocument saved = documentRepository.save(document);
        log.info("Document saved with ID: {} at path: {}", saved.getId(), storagePath);

        return DocumentUploadResponse.builder()
                .documentId(saved.getId())
                .documentName(saved.getDocumentName())
                .storagePath(storagePath)
                .verificationStatus(saved.getVerificationStatus().name())
                .message("Document uploaded successfully and is pending verification.")
                .build();
    }

    /**
     * Retrieves all documents uploaded by a client.
     *
     * @param clientId the client's ID
     * @return list of ClientDocument entities
     */
    @Transactional(readOnly = true)
    public List<ClientDocument> getClientDocuments(Long clientId) {
        return documentRepository.findByClientId(clientId);
    }

    /**
     * Validates file size and MIME type before upload.
     *
     * @param file the file to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty.");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File size exceeds the 10MB limit.");
        }
        if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                    "Invalid file type. Allowed: PDF, JPEG, PNG."
            );
        }
    }

    /**
     * Generates a unique storage path for the document.
     *
     * @param clientId         the client's ID
     * @param originalFilename the original file name
     * @return the storage path string
     */
    private String generateStoragePath(Long clientId, String originalFilename) {
        return String.format("clients/%d/documents/%s_%s",
                clientId, UUID.randomUUID(), originalFilename);
    }
}
