package com.edwardjones.advisor.repository;

import com.edwardjones.advisor.model.AccountFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository for AccountFlag persistence.
 * Story: MAP-25
 */
@Repository
public interface AccountFlagRepository extends JpaRepository<AccountFlag, Long> {

    Optional<AccountFlag> findByClientId(Long clientId);

    List<AccountFlag> findByAdvisorIdAndFlaggedTrue(Long advisorId);

    boolean existsByClientIdAndFlaggedTrue(Long clientId);
}
