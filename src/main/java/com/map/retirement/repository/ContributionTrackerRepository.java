package com.map.retirement.repository;

import com.map.retirement.model.ContributionTracker;
import com.map.retirement.model.ContributionTracker.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ContributionTracker persistence.
 * Story: MAP-21 — IRA/401(k) Contribution Progress Tracker
 * Epic:  MAP-5  — Retirement Planning Tools
 */
@Repository
public interface ContributionTrackerRepository extends JpaRepository<ContributionTracker, Long> {

    /** Find all trackers for a specific client */
    List<ContributionTracker> findByClientId(Long clientId);

    /** Find tracker for a client by account type and tax year */
    Optional<ContributionTracker> findByClientIdAndAccountTypeAndTaxYear(
            Long clientId, AccountType accountType, Integer taxYear);

    /** Find all trackers for a specific tax year where notification not yet sent */
    List<ContributionTracker> findByTaxYearAndLimitNotificationSentFalse(Integer taxYear);

    /** Update annual IRS limits for a new tax year */
    @Modifying
    @Transactional
    @Query("UPDATE ContributionTracker c SET c.annualIrsLimit = :newLimit WHERE c.accountType = :accountType AND c.taxYear = :taxYear")
    int updateAnnualLimitByAccountTypeAndYear(
            @Param("accountType") AccountType accountType,
            @Param("newLimit") BigDecimal newLimit,
            @Param("taxYear") Integer taxYear);
}
