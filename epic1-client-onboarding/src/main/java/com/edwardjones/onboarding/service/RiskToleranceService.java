package com.edwardjones.onboarding.service;

import com.edwardjones.onboarding.dto.RiskToleranceRequest;
import com.edwardjones.onboarding.model.Client;
import com.edwardjones.onboarding.model.RiskTolerance;
import com.edwardjones.onboarding.repository.ClientRepository;
import com.edwardjones.onboarding.repository.RiskToleranceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing client risk tolerance assessments.
 * Story Reference: MAP-9 — Risk tolerance questionnaire
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskToleranceService {

    private final RiskToleranceRepository riskToleranceRepository;
    private final ClientRepository clientRepository;

    /**
     * Submits or updates the risk tolerance questionnaire for a client.
     *
     * @param clientId the client's ID
     * @param request  the questionnaire answers
     * @return the saved RiskTolerance entity
     * @throws jakarta.persistence.EntityNotFoundException if client not found
     * @throws IllegalArgumentException if score is out of range
     */
    @Transactional
    public RiskTolerance submitQuestionnaire(Long clientId, RiskToleranceRequest request) {
        log.info("Submitting risk tolerance questionnaire for client ID: {}", clientId);

        if (request.getScore() < 1 || request.getScore() > 10) {
            throw new IllegalArgumentException("Risk score must be between 1 and 10.");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Client not found with ID: " + clientId
                ));

        RiskTolerance riskTolerance = riskToleranceRepository
                .findByClientId(clientId)
                .orElse(RiskTolerance.builder().client(client).build());

        riskTolerance.setScore(request.getScore());
        riskTolerance.setRiskCategory(RiskTolerance.deriveCategory(request.getScore()));
        riskTolerance.setInvestmentHorizonYears(request.getInvestmentHorizonYears());
        riskTolerance.setLossTolerancePercentage(request.getLossTolerancePercentage());
        riskTolerance.setIncomeStability(request.getIncomeStability());
        riskTolerance.setExistingInvestments(request.getExistingInvestments());
        riskTolerance.setNotes(request.getNotes());

        RiskTolerance saved = riskToleranceRepository.save(riskTolerance);
        log.info("Risk tolerance saved for client {}. Category: {}", clientId, saved.getRiskCategory());

        return saved;
    }

    /**
     * Retrieves the risk tolerance assessment for a client.
     *
     * @param clientId the client's ID
     * @return the RiskTolerance entity
     * @throws jakarta.persistence.EntityNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public RiskTolerance getRiskTolerance(Long clientId) {
        return riskToleranceRepository.findByClientId(clientId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Risk tolerance assessment not found for client ID: " + clientId
                ));
    }
}
