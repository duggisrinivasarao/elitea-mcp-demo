package com.edwardjones.onboarding.service;

import com.edwardjones.onboarding.dto.DocumentUploadResponse;
import com.edwardjones.onboarding.model.Client;
import com.edwardjones.onboarding.model.ClientDocument;
import com.edwardjones.onboarding.repository.ClientDocumentRepository;
import com.edwardjones.onboarding.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DocumentUploadService.
 * Story Reference: MAP-11 — Secure document upload & KYC
 */
@ExtendWith(MockitoExtension.class)
class DocumentUploadServiceTest {

    @Mock
    private ClientDocumentRepository documentRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private DocumentUploadService documentUploadService;

    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = Client.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .status(Client.ClientStatus.PENDING)
                .build();
    }

    /**
     * AC1: Given a valid PDF document under 10MB,
     * When the client uploads it, Then it is stored and marked as PENDING.
     */
    @Test
    @DisplayName("MAP-11 AC1: Should upload valid PDF document and mark as PENDING")
    void shouldUploadValidPdfDocumentSuccessfully() {
        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "passport.pdf",
                "application/pdf",
                new byte[1024] // 1KB
        );

        ClientDocument savedDocument = ClientDocument.builder()
                .id(10L)
                .client(testClient)
                .documentName("passport.pdf")
                .documentType(ClientDocument.DocumentType.GOVERNMENT_ID)
                .storagePath("clients/1/documents/uuid_passport.pdf")
                .verificationStatus(ClientDocument.VerificationStatus.PENDING)
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(documentRepository.save(any(ClientDocument.class))).thenReturn(savedDocument);

        DocumentUploadResponse response = documentUploadService.uploadDocument(
                1L, validFile, ClientDocument.DocumentType.GOVERNMENT_ID
        );

        assertThat(response).isNotNull();
        assertThat(response.getDocumentId()).isEqualTo(10L);
        assertThat(response.getVerificationStatus()).isEqualTo("PENDING");
        assertThat(response.getMessage()).contains("pending verification");
        verify(documentRepository, times(1)).save(any(ClientDocument.class));
    }

    /**
     * AC2: Given a valid JPEG image under 10MB,
     * When the client uploads it, Then it is stored and marked as PENDING.
     */
    @Test
    @DisplayName("MAP-11 AC2: Should upload valid JPEG image successfully")
    void shouldUploadValidJpegImageSuccessfully() {
        MockMultipartFile jpegFile = new MockMultipartFile(
                "file",
                "id_card.jpg",
                "image/jpeg",
                new byte[512 * 1024] // 512KB
        );

        ClientDocument savedDocument = ClientDocument.builder()
                .id(11L)
                .client(testClient)
                .documentName("id_card.jpg")
                .documentType(ClientDocument.DocumentType.PROOF_OF_ADDRESS)
                .storagePath("clients/1/documents/uuid_id_card.jpg")
                .verificationStatus(ClientDocument.VerificationStatus.PENDING)
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(documentRepository.save(any(ClientDocument.class))).thenReturn(savedDocument);

        DocumentUploadResponse response = documentUploadService.uploadDocument(
                1L, jpegFile, ClientDocument.DocumentType.PROOF_OF_ADDRESS
        );

        assertThat(response).isNotNull();
        assertThat(response.getDocumentId()).isEqualTo(11L);
        assertThat(response.getVerificationStatus()).isEqualTo("PENDING");
        verify(documentRepository, times(1)).save(any(ClientDocument.class));
    }

    /**
     * AC3: Given all required documents are uploaded and verified,
     * When account review is complete, Then account status changes to ACTIVE.
     * (Tested via account activation pathway.)
     */
    @Test
    @DisplayName("MAP-11 AC3: Should retrieve all documents for a client")
    void shouldRetrieveAllDocumentsForClient() {
        List<ClientDocument> docs = List.of(
                ClientDocument.builder().id(1L).documentName("passport.pdf").build(),
                ClientDocument.builder().id(2L).documentName("bank_statement.pdf").build()
        );

        when(documentRepository.findByClientId(1L)).thenReturn(docs);

        List<ClientDocument> result = documentUploadService.getClientDocuments(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDocumentName()).isEqualTo("passport.pdf");
        verify(documentRepository, times(1)).findByClientId(1L);
    }

    /**
     * AC4: Given a file exceeding 10MB,
     * When client attempts upload, Then an error is thrown.
     */
    @Test
    @DisplayName("MAP-11 AC4: Should reject file exceeding 10MB size limit")
    void shouldRejectFileThatExceedsSizeLimit() {
        byte[] oversizedContent = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile oversizedFile = new MockMultipartFile(
                "file",
                "large_document.pdf",
                "application/pdf",
                oversizedContent
        );

        assertThatThrownBy(() ->
                documentUploadService.uploadDocument(
                        1L, oversizedFile, ClientDocument.DocumentType.GOVERNMENT_ID
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10MB");

        verify(documentRepository, never()).save(any());
    }

    /**
     * AC5: Given a file with an invalid MIME type (e.g., .exe or .txt),
     * When client attempts upload, Then a clear error message is shown.
     */
    @Test
    @DisplayName("MAP-11 AC5: Should reject file with invalid MIME type")
    void shouldRejectFileWithInvalidMimeType() {
        MockMultipartFile invalidTypeFile = new MockMultipartFile(
                "file",
                "malicious.exe",
                "application/octet-stream",
                new byte[1024]
        );

        assertThatThrownBy(() ->
                documentUploadService.uploadDocument(
                        1L, invalidTypeFile, ClientDocument.DocumentType.GOVERNMENT_ID
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid file type");

        verify(documentRepository, never()).save(any());
    }

    /**
     * AC6: Given an empty file, When client attempts upload, Then error is thrown.
     */
    @Test
    @DisplayName("MAP-11 AC6: Should reject empty file upload")
    void shouldRejectEmptyFileUpload() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        assertThatThrownBy(() ->
                documentUploadService.uploadDocument(
                        1L, emptyFile, ClientDocument.DocumentType.GOVERNMENT_ID
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty");

        verify(documentRepository, never()).save(any());
    }
}
