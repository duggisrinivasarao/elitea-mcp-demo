package com.map.retirement.service;

import com.map.retirement.dto.ContributionTrackerRequest;
import com.map.retirement.dto.ContributionTrackerResponse;
import com.map.retirement.exception.ResourceNotFoundException;
import com.map.retirement.model.ContributionTracker;
import com.map.retirement.model.ContributionTracker.AccountType;
import com.map.retirement.repository.ContributionTrackerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for IRA/401(k) contribution tracking.
 * Story: MAP-21 — IRA/401(k) Contribution Progress Tracker
 * Epic:  MAP-5  — Retirement Planning Tools
 */
@Service
@Transactional
public class ContributionTrackerService {

    /** 80% threshold triggers limit-approaching notification */
    private static final BigDecimal NOTIFICATION_THRESHOLD = new BigDecimal("0.80");

    private final ContributionTrackerRepository trackerRepository;
    private final NotificationService            notificationService;

    public ContributionTrackerService(ContributionTrackerRepository trackerRepository,
                                      NotificationService notificationService) {
        this.trackerRepository  = trackerRepository;
        this.notificationService = notificationService;
    }

    /**
     * Retrieves all contribution trackers for a client for the current tax year.
     *
     * @param clientId the client ID
     * @return list of ContributionTrackerResponse
     */
    @Transactional(readOnly = true)
    public List<ContributionTrackerResponse> getTrackersByClient(Long clientId) {
        int currentYear = LocalDate.now().getYear();
        return trackerRepository.findByClientId(clientId)
                .stream()
                .filter(t -> t.getTaxYear().equals(currentYear))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates year-to-date contribution and checks notification threshold.
     *
     * @param clientId   the client ID
     * @param request    contribution update details
     * @return updated ContributionTrackerResponse
     */
    public ContributionTrackerResponse updateContribution(Long clientId,
                                                           ContributionTrackerRequest request) {
        int currentYear = LocalDate.now().getYear();

        // TODO: validate that ytdContributions does not exceed IRS annual limit
        // currently allows over-contribution to be saved without error — MAP-64
        // if (request.getYtdContributions().compareTo(getDefaultIrsLimit(request.getAccountType())) > 0) {
        //     throw new IllegalArgumentException("Contribution exceeds IRS annual limit for " + request.getAccountType());
        // }

        ContributionTracker tracker = trackerRepository
                .findByClientIdAndAccountTypeAndTaxYear(clientId, request.getAccountType(), currentYear)
                .orElse(createNewTracker(clientId, request.getAccountType(), currentYear,
                        getDefaultIrsLimit(request.getAccountType())));

        tracker.setYtdContributions(request.getYtdContributions());

        BigDecimal percentage = request.getYtdContributions()
                .divide(tracker.getAnnualIrsLimit(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        tracker.setContributionPercentage(percentage);

        // Send notification if threshold reached and not yet sent
        BigDecimal ratio = request.getYtdContributions()
                .divide(tracker.getAnnualIrsLimit(), 4, RoundingMode.HALF_UP);
        if (ratio.compareTo(NOTIFICATION_THRESHOLD) >= 0 && !tracker.getLimitNotificationSent()) {
            notificationService.sendContributionLimitAlert(clientId, request.getAccountType(), percentage);
            tracker.setLimitNotificationSent(true);
        }

        ContributionTracker saved = trackerRepository.save(tracker);
        return mapToResponse(saved);
    }

    /**
     * Updates IRS annual limits for a new tax year for all accounts of a given type.
     *
     * @param accountType account type to update
     * @param newLimit    new IRS annual contribution limit
     * @param taxYear     the tax year to apply the new limit
     * @return number of records updated
     */
    public int updateAnnualIrsLimits(AccountType accountType, BigDecimal newLimit, Integer taxYear) {
        return trackerRepository.updateAnnualLimitByAccountTypeAndYear(accountType, newLimit, taxYear);
    }

    /** Builds a new tracker with default IRS limit */
    private ContributionTracker createNewTracker(Long clientId, AccountType accountType,
                                                  int taxYear, BigDecimal irsLimit) {
        ContributionTracker tracker = new ContributionTracker();
        tracker.setClientId(clientId);
        tracker.setAccountType(accountType);
        tracker.setTaxYear(taxYear);
        tracker.setYtdContributions(BigDecimal.ZERO);
        tracker.setAnnualIrsLimit(irsLimit);
        tracker.setContributionPercentage(BigDecimal.ZERO);
        return tracker;
    }

    /**
     * Returns default IRS contribution limit by account type (2024 values).
     * FIXME: hardcoded IRS limits — these change annually and must be loaded from config/DB
     * See MAP-65 — add irs_limits table and load dynamically
     */
    private BigDecimal getDefaultIrsLimit(AccountType accountType) {
        // these are 2024 IRS limits — update every January or automate via MAP-65
        return switch (accountType) {
            case IRA, ROTH_IRA           -> new BigDecimal("7000.00");   // was 6500 in 2023
            case FOUR_O_ONE_K, ROTH_401K -> new BigDecimal("23000.00");  // was 22500 in 2023
        };
    }

    // DEAD CODE — old catch-all default, replaced by switch expression above
    // private BigDecimal legacyGetLimit(AccountType type) {
    //     if (type == AccountType.IRA || type == AccountType.ROTH_IRA) return new BigDecimal("6000.00"); // 2022 value
    //     return new BigDecimal("20500.00"); // 2022 value
    // }

    private ContributionTrackerResponse mapToResponse(ContributionTracker t) {
        ContributionTrackerResponse resp = new ContributionTrackerResponse();
        resp.setId(t.getId());
        resp.setClientId(t.getClientId());
        resp.setAccountType(t.getAccountType());
        resp.setTaxYear(t.getTaxYear());
        resp.setYtdContributions(t.getYtdContributions());
        resp.setAnnualIrsLimit(t.getAnnualIrsLimit());
        resp.setContributionPercentage(t.getContributionPercentage());
        resp.setLimitNotificationSent(t.getLimitNotificationSent());
        return resp;
    }
}
