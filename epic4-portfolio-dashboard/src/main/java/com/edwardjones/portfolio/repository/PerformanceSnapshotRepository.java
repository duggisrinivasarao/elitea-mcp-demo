package com.edwardjones.portfolio.repository;

import com.edwardjones.portfolio.model.PerformanceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for PerformanceSnapshot data access.
 * Story Reference: MAP-14 — Performance Charts over Time
 */
@Repository
public interface PerformanceSnapshotRepository extends JpaRepository<PerformanceSnapshot, Long> {

    /** Returns performance snapshots for a client within a date range. */
    List<PerformanceSnapshot> findByClientIdAndSnapshotDateBetweenOrderBySnapshotDateAsc(
            String clientId, LocalDate from, LocalDate to);

    /** Returns all snapshots for a client ordered by date. */
    List<PerformanceSnapshot> findByClientIdOrderBySnapshotDateAsc(String clientId);

    /** Returns the most recent snapshot for a client. */
    PerformanceSnapshot findTopByClientIdOrderBySnapshotDateDesc(String clientId);
}
