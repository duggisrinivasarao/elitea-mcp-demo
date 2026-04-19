package com.edwardjones.onboarding.service;

import com.edwardjones.onboarding.dto.ClientRegistrationRequest;
import com.edwardjones.onboarding.dto.ClientRegistrationResponse;
import com.edwardjones.onboarding.model.Client;
import com.edwardjones.onboarding.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClientOnboardingService.
 * Story Reference: MAP-8 — Register and complete financial profile
 */
@ExtendWith(MockitoExtension.class)
class ClientOnboardingServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AdvisorAssignmentService advisorAssignmentService;

    @InjectMocks
    private ClientOnboardingService onboardingService;

    private ClientRegistrationRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = ClientRegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("5551234567")
                .dateOfBirth(LocalDate.of(1985, 6, 15))
                .annualIncome(95000.0)
                .netWorth(250000.0)
                .build();
    }

    /**
     * AC: Given valid details, When client registers, Then client is saved and response returned.
     */
    @Test
    @DisplayName("MAP-8 AC1: Should register client successfully with valid details")
    void shouldRegisterClientSuccessfully() {
        Client savedClient = Client.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("5551234567")
                .dateOfBirth(LocalDate.of(1985, 6, 15))
                .status(Client.ClientStatus.PENDING)
                .build();

        when(clientRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);
        doNothing().when(advisorAssignmentService).assignAdvisor(any(Client.class));

        ClientRegistrationResponse response = onboardingService.registerClient(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getClientId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(response.getStatus()).isEqualTo("PENDING");
        verify(clientRepository, times(1)).save(any(Client.class));
        verify(advisorAssignmentService, times(1)).assignAdvisor(any(Client.class));
    }

    /**
     * AC: Given duplicate email, When client registers, Then exception is thrown.
     */
    @Test
    @DisplayName("MAP-8 AC2: Should throw exception when email already registered")
    void shouldThrowExceptionForDuplicateEmail() {
        when(clientRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> onboardingService.registerClient(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already registered");

        verify(clientRepository, never()).save(any());
    }

    /**
     * AC: Given registered client, When activate is called, Then status becomes ACTIVE.
     */
    @Test
    @DisplayName("MAP-8 AC3: Should activate client account successfully")
    void shouldActivateClientSuccessfully() {
        Client client = Client.builder()
                .id(1L)
                .status(Client.ClientStatus.PENDING)
                .build();

        when(clientRepository.findById(1L)).thenReturn(java.util.Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        onboardingService.activateClient(1L);

        assertThat(client.getStatus()).isEqualTo(Client.ClientStatus.ACTIVE);
        verify(clientRepository).save(client);
    }
}
