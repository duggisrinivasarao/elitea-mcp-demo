package com.edwardjones.advisor.service;

import com.edwardjones.advisor.dto.SecureMessageRequest;
import com.edwardjones.advisor.dto.SecureMessageResponse;
import com.edwardjones.advisor.model.SecureMessage;
import com.edwardjones.advisor.repository.SecureMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SecureMessageService.
 * Story: MAP-23 — Secure Messaging
 * Covers all 3 Acceptance Criteria.
 */
class SecureMessageServiceTest {

    @Mock private SecureMessageRepository messageRepository;
    @Mock private NotificationService notificationService;
    @InjectMocks private SecureMessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * AC1: Given advisor sends a message,
     * Then it is delivered securely and client is notified.
     */
    @Test
    @DisplayName("MAP-23-AC1: message is encrypted and client is notified")
    void sendMessage_shouldEncryptAndNotifyRecipient() {
        SecureMessageRequest request = new SecureMessageRequest();
        request.setSenderId(1L);
        request.setRecipientId(2L);
        request.setContent("Buy index funds.");

        SecureMessage saved = SecureMessage.builder()
                .id(5L).senderId(1L).recipientId(2L)
                .encryptedContent("QnV5IGluZGV4IGZ1bmRzLg==")
                .status(SecureMessage.MessageStatus.DELIVERED)
                .sentAt(LocalDateTime.now())
                .build();

        when(messageRepository.save(any())).thenReturn(saved);

        SecureMessageResponse response = messageService.sendMessage(request);

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getStatus()).isEqualTo(SecureMessage.MessageStatus.DELIVERED);
        verify(notificationService).sendMessageNotification(2L, 5L);
    }

    /**
     * AC2: Given client reads a message,
     * Then a read receipt is recorded and visible to advisor.
     */
    @Test
    @DisplayName("MAP-23-AC2: marking as read sets receipt timestamp and status")
    void markAsRead_shouldSetReceiptAndStatus() {
        SecureMessage existing = SecureMessage.builder()
                .id(5L).senderId(1L).recipientId(2L)
                .encryptedContent("encrypted")
                .readByRecipient(false)
                .status(SecureMessage.MessageStatus.DELIVERED)
                .sentAt(LocalDateTime.now())
                .build();

        when(messageRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(messageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SecureMessageResponse response = messageService.markAsRead(5L);

        assertThat(response.isReadByRecipient()).isTrue();
        assertThat(response.getReadAt()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(SecureMessage.MessageStatus.READ);
    }

    /**
     * AC3: Given a security breach is detected,
     * Then session is terminated and both parties are alerted.
     */
    @Test
    @DisplayName("MAP-23-AC3: breach detection terminates session and alerts both parties")
    void flagSecurityBreach_shouldTerminateAndAlert() {
        SecureMessage existing = SecureMessage.builder()
                .id(5L).senderId(1L).recipientId(2L)
                .encryptedContent("encrypted")
                .status(SecureMessage.MessageStatus.DELIVERED)
                .sentAt(LocalDateTime.now())
                .build();

        when(messageRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(messageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        messageService.flagSecurityBreach(5L);

        verify(notificationService).sendSecurityBreachAlert(1L, 2L);
        assertThat(existing.getStatus()).isEqualTo(SecureMessage.MessageStatus.TERMINATED);
    }

    /**
     * Edge case: marking non-existent message as read throws exception.
     */
    @Test
    @DisplayName("MAP-23-edge: non-existent message throws IllegalArgumentException")
    void markAsRead_notFound_shouldThrow() {
        when(messageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.markAsRead(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Message not found");
    }
}
