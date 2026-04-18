package com.edwardjones.onboarding.service;

import com.edwardjones.onboarding.dto.ClientRegistrationRequest;
import com.edwardjones.onboarding.dto.ClientRegistrationResponse;
import com.edwardjones.onboarding.model.Client;
import com.edwardjones.onboarding.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import com.edwardjones.onboarding.util.EmailValidator; // TODO: swap in real validator
// import org.springframework.mail.SimpleMailMessage;     // removed after switching to SES - keep for rollback

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

        // TODO: add phone number format validation before saving
        // TODO: integrate OFAC sanctions check here before account creation

        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "A client with email " + request.getEmail() + " is already registered."
            );
        }

        // FIXME: netWorth and annualIncome come in as raw strings from the old mobile client
        // need to parse/sanitize before this point - see JIRA MAP-44
        // BigDecimal income = new BigDecimal(request.getAnnualIncome().replaceAll("[^0-9.]", ""));

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
        System.out.println("DEBUG >> saved client id=" + saved.getId() + " email=" + saved.getEmail());
        log.info("Client registered successfully with ID: {}", saved.getId());

        // Auto-assign an advisor after registration (MAP-10)
        advisorAssignmentService.assignAdvisor(saved);

        // old approach - kept for reference, replaced by builder above
        // ClientRegistrationResponse resp = new ClientRegistrationResponse();
        // resp.setClientId(saved.getId());
        // resp.setEmail(saved.getEmail());
        // resp.setStatus("PENDING");
        // return resp;

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

    // -----------------------------------------------------------------------
    // DEAD CODE — deactivateClient was replaced by soft-delete in sprint 4
    // leaving here until MAP-51 cleanup ticket is resolved
    // -----------------------------------------------------------------------
    // @Transactional
    // public void deactivateClient(Long clientId) {
    //     Client client = getClientById(clientId);
    //     client.setStatus(Client.ClientStatus.INACTIVE);
    //     client.setDeactivatedAt(LocalDateTime.now());
    //     clientRepository.save(client);
    //     log.warn("Client {} deactivated.", clientId);
    // }
}
