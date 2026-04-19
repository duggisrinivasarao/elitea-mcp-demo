package com.edwardjones.portfolio.repository;

import com.edwardjones.portfolio.model.PortfolioAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PortfolioAlert data access.
 * Story Reference: MAP-17 — Portfolio Alert Notifications
 */
@Repository
public interface PortfolioAlertRepository extends JpaRepository<PortfolioAlert, Long> {

    /** Returns all active alerts for a specific client. */
    List<PortfolioAlert> findByClientIdAndEnabledTrue(String clientId);

    /** Returns all alerts (enabled or not) for a client. */
    List<PortfolioAlert> findByClientId(String clientId);

    /** Finds a specific alert by client and alert ID. */
    Optional<PortfolioAlert> findByIdAndClientId(Long id, String clientId);
}
