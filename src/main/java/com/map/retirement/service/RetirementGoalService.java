package com.map.retirement.service;

import com.map.retirement.dto.RetirementGoalRequest;
import com.map.retirement.dto.RetirementGoalResponse;
import com.map.retirement.exception.InvalidRetirementGoalException;
import com.map.retirement.model.RetirementGoal;
import com.map.retirement.repository.RetirementGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for retirement goal calculation and management.
 * Story: MAP-18 — Client Retirement Goal Definition & Savings Calculator
 * Epic:  MAP-5  — Retirement Planning Tools
 *
 * Assumptions:
 * - Life expectancy constant = 85 years (configurable via properties)
 * - Annual inflation rate    = 3%
 * - Safe withdrawal rate     = 4%
 */
@Service
@Transactional
public class RetirementGoalService {

    private static final int    LIFE_EXPECTANCY_YEARS = 85;
    private static final double SAFE_WITHDRAWAL_RATE  = 0.04;
    private static final double ANNUAL_INFLATION_RATE = 0.03;

    private final RetirementGoalRepository retirementGoalRepository;

    public RetirementGoalService(RetirementGoalRepository retirementGoalRepository) {
        this.retirementGoalRepository = retirementGoalRepository;
    }

    /**
     * Creates or updates a client's retirement goal and calculates required savings.
     *
     * @param clientId the ID of the client
     * @param request  the retirement goal input data
     * @return calculated RetirementGoalResponse
     * @throws InvalidRetirementGoalException if retirement age <= current age
     */
    public RetirementGoalResponse saveRetirementGoal(Long clientId, RetirementGoalRequest request) {
        validateRetirementAge(request.getCurrentAge(), request.getTargetRetirementAge());

        RetirementGoal goal = retirementGoalRepository
                .findTopByClientIdOrderByCreatedAtDesc(clientId)
                .orElse(new RetirementGoal());

        goal.setClientId(clientId);
        goal.setCurrentAge(request.getCurrentAge());
        goal.setTargetRetirementAge(request.getTargetRetirementAge());
        goal.setMonthlyIncomeNeeded(request.getMonthlyIncomeNeeded());
        goal.setCurrentSavings(request.getCurrentSavings());

        BigDecimal requiredTarget = calculateRequiredSavingsTarget(request);
        BigDecimal monthlyRequired = calculateMonthlySavingsRequired(request, requiredTarget);

        goal.setRequiredSavingsTarget(requiredTarget);
        goal.setProjectedMonthlySavingsRequired(monthlyRequired);

        RetirementGoal saved = retirementGoalRepository.save(goal);
        return mapToResponse(saved);
    }

    /**
     * Retrieves all retirement goals for a client.
     *
     * @param clientId the client ID
     * @return list of RetirementGoalResponse
     */
    @Transactional(readOnly = true)
    public List<RetirementGoalResponse> getGoalsByClientId(Long clientId) {
        return retirementGoalRepository.findByClientId(clientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Validates that retirement age is strictly greater than current age.
     *
     * @param currentAge         client's current age
     * @param targetRetirementAge target retirement age
     * @throws InvalidRetirementGoalException if invalid
     */
    private void validateRetirementAge(int currentAge, int targetRetirementAge) {
        if (targetRetirementAge <= currentAge) {
            throw new InvalidRetirementGoalException(
                    "Target retirement age must be greater than current age. " +
                    "Current: " + currentAge + ", Target: " + targetRetirementAge);
        }
    }

    /**
     * Calculates total required savings at retirement using the safe withdrawal rate formula.
     * Formula: (monthlyIncomeNeeded * 12) / safeWithdrawalRate
     */
    private BigDecimal calculateRequiredSavingsTarget(RetirementGoalRequest request) {
        BigDecimal annualIncomeNeeded = request.getMonthlyIncomeNeeded()
                .multiply(BigDecimal.valueOf(12));
        return annualIncomeNeeded
                .divide(BigDecimal.valueOf(SAFE_WITHDRAWAL_RATE), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the required monthly savings to reach the target.
     * Formula: (requiredTarget - currentSavings) / (yearsToRetirement * 12)
     */
    private BigDecimal calculateMonthlySavingsRequired(RetirementGoalRequest request, BigDecimal requiredTarget) {
        int yearsToRetirement = request.getTargetRetirementAge() - request.getCurrentAge();
        BigDecimal gap = requiredTarget.subtract(request.getCurrentSavings());
        if (gap.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        int months = yearsToRetirement * 12;
        return gap.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
    }

    /** Maps entity to response DTO */
    private RetirementGoalResponse mapToResponse(RetirementGoal goal) {
        RetirementGoalResponse resp = new RetirementGoalResponse();
        resp.setId(goal.getId());
        resp.setClientId(goal.getClientId());
        resp.setCurrentAge(goal.getCurrentAge());
        resp.setTargetRetirementAge(goal.getTargetRetirementAge());
        resp.setMonthlyIncomeNeeded(goal.getMonthlyIncomeNeeded());
        resp.setCurrentSavings(goal.getCurrentSavings());
        resp.setRequiredSavingsTarget(goal.getRequiredSavingsTarget());
        resp.setProjectedMonthlySavingsRequired(goal.getProjectedMonthlySavingsRequired());
        return resp;
    }
}
