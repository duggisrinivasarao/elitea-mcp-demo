package com.edwardjones.advisor.repository;

import com.edwardjones.advisor.model.SecureMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for SecureMessage persistence.
 * Story: MAP-23
 */
@Repository
public interface SecureMessageRepository extends JpaRepository<SecureMessage, Long> {

    List<SecureMessage> findBySenderIdAndRecipientId(Long senderId, Long recipientId);

    List<SecureMessage> findByRecipientIdAndReadByRecipientFalse(Long recipientId);

    List<SecureMessage> findByRecipientIdOrderBySentAtDesc(Long recipientId);
}
