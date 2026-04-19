package com.edwardjones.onboarding.service;

import com.edwardjones.onboarding.model.Client;
import com.edwardjones.onboarding.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service responsible for automatically assigning financial advisors to new clients.
 * Story Reference: MAP-10 — Automatic advisor assignment
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdvisorAssignmentService {

    private final ClientRepository clientRepository;

    // Simulated pool of available advisor IDs (replace with AdvisorRepository in production)
    private static final List<Long> ADVISOR_POOL = List.of(101L, 102L, 103L, 104L, 105L);

    /**
     * Automatically assigns the least-loaded advisor to a newly registered client.
     * Uses a load-balancing strategy based on current client count per advisor.
     *
     * @param client the newly registered client to assign
     */
    @Transactional
    public void assignAdvisor(Client client) {
        log.info("Assigning advisor to client ID: {}", client.getId());

        Long selectedAdvisorId = selectLeastLoadedAdvisor();

        client.setAssignedAdvisorId(selectedAdvisorId);
        clientRepository.save(client);

        log.info("Advisor {} assigned to client {}", selectedAdvisorId, client.getId());
    }

    /**
     * Manually reassigns a client to a different advisor.
     *
     * @param clientId  the client's ID
     * @param advisorId the new advisor's ID
     * @throws jakarta.persistence.EntityNotFoundException if client not found
     * @throws IllegalArgumentException if advisor ID is not in the pool
     */
    @Transactional
    public void reassignAdvisor(Long clientId, Long advisorId) {
        if (!ADVISOR_POOL.contains(advisorId)) {
            throw new IllegalArgumentException("Advisor ID " + advisorId + " is not available.");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Client not found with ID: " + clientId
                ));

        Long previousAdvisor = client.getAssignedAdvisorId();
        client.setAssignedAdvisorId(advisorId);
        clientRepository.save(client);

        log.info("Client {} reassigned from advisor {} to advisor {}",
                clientId, previousAdvisor, advisorId);
    }

    /**
     * Returns the advisor ID with the fewest assigned clients.
     *
     * @return advisor ID with minimum load
     */
    private Long selectLeastLoadedAdvisor() {
        return ADVISOR_POOL.stream()
                .min((a, b) -> Math.toIntExact(
                        clientRepository.countByAdvisorId(a) - clientRepository.countByAdvisorId(b)
                ))
                .orElse(ADVISOR_POOL.get(0));
    }

    /**
     * Gets the advisor ID currently assigned to a client.
     *
     * @param clientId the client's ID
     * @return the assigned advisor's ID
     */
    @Transactional(readOnly = true)
    public Long getAssignedAdvisor(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Client not found with ID: " + clientId
                ));
        return client.getAssignedAdvisorId();
    }
}
