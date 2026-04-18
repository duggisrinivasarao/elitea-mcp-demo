package com.edwardjones.onboarding.controller;

import com.edwardjones.onboarding.dto.*;
import com.edwardjones.onboarding.model.ClientDocument;
import com.edwardjones.onboarding.model.RiskTolerance;
import com.edwardjones.onboarding.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for client onboarding operations.
 * Story References: MAP-8, MAP-9, MAP-10, MAP-11
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientOnboardingController {

    private final ClientOnboardingService onboardingService;
    private final RiskToleranceService riskToleranceService;
    private final AdvisorAssignmentService advisorAssignmentService;
    private final DocumentUploadService documentUploadService;

    /**
     * Registers a new client with Edward Jones.
     * MAP-8: Register and complete financial profile
     *
     * @param request the client registration details
     * @return 201 Created with client registration response
     */
    @PostMapping("/register")
    public ResponseEntity<ClientRegistrationResponse> registerClient(
            @Valid @RequestBody ClientRegistrationRequest request) {
        log.info("POST /api/v1/clients/register - email: {}", request.getEmail());
        // TODO: add rate limiting annotation here — brute-force registration possible
        // @RateLimiter(name = "registrationLimiter") — Resilience4j not yet configured
        ClientRegistrationResponse response = onboardingService.registerClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Submits risk tolerance questionnaire for a client.
     * MAP-9: Risk tolerance questionnaire
     *
     * @param clientId the client's ID
     * @param request  the questionnaire answers
     * @return 200 OK with saved risk tolerance
     */
    @PostMapping("/{clientId}/risk-tolerance")
    public ResponseEntity<RiskTolerance> submitRiskTolerance(
            @PathVariable Long clientId,
            @Valid @RequestBody RiskToleranceRequest request) {
        log.info("POST /api/v1/clients/{}/risk-tolerance", clientId);
        // FIXME: returning raw JPA entity — should return a DTO to avoid exposing internal fields
        // tracked in MAP-46 refactoring backlog
        RiskTolerance saved = riskToleranceService.submitQuestionnaire(clientId, request);
        return ResponseEntity.ok(saved);
    }

    /**
     * Gets the assigned advisor for a client.
     * MAP-10: Automatic advisor assignment
     *
     * @param clientId the client's ID
     * @return 200 OK with assigned advisor ID
     */
    @GetMapping("/{clientId}/advisor")
    public ResponseEntity<Long> getAssignedAdvisor(@PathVariable Long clientId) {
        log.info("GET /api/v1/clients/{}/advisor", clientId);
        Long advisorId = advisorAssignmentService.getAssignedAdvisor(clientId);
        return ResponseEntity.ok(advisorId);
    }

    /**
     * Uploads a KYC document for a client.
     * MAP-11: Secure document upload & KYC
     *
     * @param clientId     the client's ID
     * @param file         the document file
     * @param documentType the type of document
     * @return 201 Created with upload response
     */
    @PostMapping(value = "/{clientId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentUploadResponse> uploadDocument(
            @PathVariable Long clientId,
            @RequestPart("file") MultipartFile file,
            @RequestParam("documentType") ClientDocument.DocumentType documentType) {
        log.info("POST /api/v1/clients/{}/documents - type: {}", clientId, documentType);
        // TODO: add @PreAuthorize("hasRole('CLIENT') and #clientId == authentication.principal.id")
        // currently any authenticated user can upload docs for any clientId
        DocumentUploadResponse response = documentUploadService.uploadDocument(clientId, file, documentType);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
