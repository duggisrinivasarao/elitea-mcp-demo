package com.edwardjones.portfolio.repository;

import com.edwardjones.portfolio.model.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for Holding data access.
 * Story Reference: MAP-13 — Real-time Portfolio Holdings Dashboard
 */
@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

    /** Returns all holdings for a given client. */
    List<Holding> findByClientId(String clientId);

    /** Returns holdings filtered by asset type. */
    List<Holding> findByClientIdAndAssetType(String clientId, Holding.AssetType assetType);

    /** Calculates total portfolio value for a client. */
    @Query("SELECT SUM(h.currentValue) FROM Holding h WHERE h.clientId = :clientId")
    BigDecimal sumCurrentValueByClientId(@Param("clientId") String clientId);

    /** Returns all holdings across all clients for an advisor. */
    @Query("SELECT h FROM Holding h WHERE h.clientId IN :clientIds")
    List<Holding> findByClientIds(@Param("clientIds") List<String> clientIds);
}
