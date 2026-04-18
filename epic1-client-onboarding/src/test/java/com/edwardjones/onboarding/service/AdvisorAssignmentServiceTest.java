package com.edwardjones.onboarding.service;

import com.edwardjones.onboarding.model.Client;
import com.edwardjones.onboarding.repository.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdvisorAssignmentService.
 * Story Reference: MAP-10 — Automatic advisor assignment
 */
@ExtendWith(MockitoExtension.class)
class AdvisorAssignmentServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private AdvisorAssignmentService advisorAssignmentService;

    /**
     * AC: Given a new client, When assignAdvisor is called, Then an advisor is assigned.
     */
    @Test
    @DisplayName("MAP-10 AC1: Should assign advisor to new client")
    void shouldAssignAdvisorToNewClient() {
        Client client = Client.builder().id(1L).build();

        when(clientRepository.countByAdvisorId(any())).thenReturn(0L);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        advisorAssignmentService.assignAdvisor(client);

        assertThat(client.getAssignedAdvisorId()).isNotNull();
        verify(clientRepository, times(1)).save(client);
    }

    /**
     * AC: Given valid advisorId, When reassignAdvisor is called, Then advisor updated.
     */
    @Test
    @DisplayName("MAP-10 AC2: Should reassign client to valid advisor")
    void shouldReassignClientToValidAdvisor() {
        Client client = Client.builder().id(1L).assignedAdvisorId(101L).build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        advisorAssignmentService.reassignAdvisor(1L, 102L);

        assertThat(client.getAssignedAdvisorId()).isEqualTo(102L);
    }

    /**
     * AC: Given invalid advisorId, When reassignAdvisor is called, Then exception thrown.
     */
    @Test
    @DisplayName("MAP-10 AC3: Should throw exception for unknown advisor ID")
    void shouldThrowExceptionForUnknownAdvisor() {
        assertThatThrownBy(() -> advisorAssignmentService.reassignAdvisor(1L, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not available");
    }
}
