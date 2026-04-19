package com.edwardjones.onboarding.service;

import com.edwardjones.onboarding.dto.ClientRegistrationRequest;
import com.edwardjones.onboarding.dto.ClientRegistrationResponse;
import com.edwardjones.onboarding.model.Client;
import com.edwardjones.onboarding.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling client registration and profile management.
 * Story Reference: MAP-8 — Register and complete financial profile
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientOnboardingService {

    private final ClientRepository clientRepository;
    private final AdvisorAssignmentService advisorAssignmentService;

    /**
     * Registers a new client and initiates the onboarding process.
     *
     * @param request the registration request containing client details
     * @return ClientRegistrationResponse with client ID and status
     * @throws IllegalArgumentException if email is already registered
     */
    @Transactional
    public ClientRegistrationResponse registerClient(ClientRegistrationRequest request) {
        log.info("Registering new client with email: {}", request.getEmail());

        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "A client with email " + request.getEmail() + " is already registered."
            );
        }

        Client client = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .annualIncome(request.getAnnualIncome())
                .netWorth(request.getNetWorth())
                .status(Client.ClientStatus.PENDING)
                .build();

        Client saved = clientRepository.save(client);
        log.info("Client registered successfully with ID: {}", saved.getId());

        // Auto-assign an advisor after registration (MAP-10)
        advisorAssignmentService.assignAdvisor(saved);

        return ClientRegistrationResponse.builder()
                .clientId(saved.getId())
                .fullName(saved.getFirstName() + " " + saved.getLastName())
                .email(saved.getEmail())
                .status(saved.getStatus().name())
                .message("Registration successful. An advisor will be assigned shortly.")
                .build();
    }

    /**
     * Retrieves a client by their ID.
     *
     * @param clientId the client's unique identifier
     * @return the Client entity
     * @throws jakarta.persistence.EntityNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public Client getClientById(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Client not found with ID: " + clientId
                ));
    }

    /**
     * Activates a client account after KYC verification is complete.
     *
     * @param clientId the client's ID to activate
     */
    @Transactional
    public void activateClient(Long clientId) {
        Client client = getClientById(clientId);
        client.setStatus(Client.ClientStatus.ACTIVE);
        clientRepository.save(client);
        log.info("Client {} has been activated.", clientId);
    }
}
