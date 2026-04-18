package com.edwardjones.advisor.service;

import com.edwardjones.advisor.dto.SecureMessageRequest;
import com.edwardjones.advisor.dto.SecureMessageResponse;
import com.edwardjones.advisor.model.SecureMessage;
import com.edwardjones.advisor.repository.SecureMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service handling encrypted advisor-client messaging.
 * Story: MAP-23 — Secure Messaging
 */
@Service
@RequiredArgsConstructor
public class SecureMessageService {

    private final SecureMessageRepository messageRepository;
    private final NotificationService notificationService;

    /**
     * Sends a secure encrypted message from advisor to client.
     * AC: Message is delivered securely and client is notified.
     *
     * @param request the message send request
     * @return SecureMessageResponse with delivery details
     */
    @Transactional
    public SecureMessageResponse sendMessage(SecureMessageRequest request) {
        String encrypted = encrypt(request.getContent());
        SecureMessage message = SecureMessage.builder()
                .senderId(request.getSenderId())
                .recipientId(request.getRecipientId())
                .encryptedContent(encrypted)
                .status(SecureMessage.MessageStatus.DELIVERED)
                .build();
        SecureMessage saved = messageRepository.save(message);
        notificationService.sendMessageNotification(saved.getRecipientId(), saved.getId());
        return toResponse(saved);
    }

    /**
     * Marks a message as read and records the read receipt.
     * AC: Read receipt is recorded and visible to the advisor.
     *
     * @param messageId the ID of the message to mark as read
     * @return updated SecureMessageResponse
     */
    @Transactional
    public SecureMessageResponse markAsRead(Long messageId) {
        SecureMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));
        message.setReadByRecipient(true);
        message.setReadAt(LocalDateTime.now());
        message.setStatus(SecureMessage.MessageStatus.READ);
        return toResponse(messageRepository.save(message));
    }

    /**
     * Flags a message and terminates session on security breach.
     * AC: On unauthorized access, session is terminated and both parties alerted.
     *
     * @param messageId the ID of the compromised message
     */
    @Transactional
    public void flagSecurityBreach(Long messageId) {
        SecureMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));
        message.setStatus(SecureMessage.MessageStatus.TERMINATED);
        messageRepository.save(message);
        notificationService.sendSecurityBreachAlert(message.getSenderId(), message.getRecipientId());
    }

    /**
     * Retrieves all unread messages for a recipient.
     *
     * @param recipientId the recipient's ID
     * @return list of unread SecureMessageResponses
     */
    public List<SecureMessageResponse> getUnreadMessages(Long recipientId) {
        return messageRepository.findByRecipientIdAndReadByRecipientFalse(recipientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Encrypts message content using Base64 encoding (placeholder for real encryption).
     */
    private String encrypt(String content) {
        return Base64.getEncoder().encodeToString(content.getBytes());
    }

    private SecureMessageResponse toResponse(SecureMessage m) {
        return SecureMessageResponse.builder()
                .id(m.getId())
                .senderId(m.getSenderId())
                .recipientId(m.getRecipientId())
                .readByRecipient(m.isReadByRecipient())
                .readAt(m.getReadAt())
                .status(m.getStatus())
                .sentAt(m.getSentAt())
                .build();
    }
}
