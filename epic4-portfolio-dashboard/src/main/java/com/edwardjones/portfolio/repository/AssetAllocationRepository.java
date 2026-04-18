package com.edwardjones.portfolio.repository;

import com.edwardjones.portfolio.model.AssetAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for AssetAllocation data access.
 * Story Reference: MAP-15 — Asset Allocation Breakdown
 */
@Repository
public interface AssetAllocationRepository extends JpaRepository<AssetAllocation, Long> {

    /** Finds the target allocation configuration for a specific client. */
    Optional<AssetAllocation> findByClientId(String clientId);
}
