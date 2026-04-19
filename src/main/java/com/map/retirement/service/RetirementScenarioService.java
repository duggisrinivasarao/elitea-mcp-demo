package com.map.retirement.service;

import com.map.retirement.dto.ScenarioRequest;
import com.map.retirement.dto.ScenarioComparisonResponse;
import com.map.retirement.dto.ScenarioResponse;
import com.map.retirement.exception.ResourceNotFoundException;
import com.map.retirement.model.RetirementGoal;
import com.map.retirement.model.RetirementScenario;
import com.map.retirement.model.RetirementScenario.ScenarioType;
import com.map.retirement.repository.RetirementGoalRepository;
import com.map.retirement.repository.RetirementScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for retirement scenario simulation.
 * Story: MAP-19 — Retirement Scenario Simulation
 * Epic:  MAP-5  — Retirement Planning Tools
 *
 * Annual return rates by scenario type:
 * - CONSERVATIVE : 4%
 * - MODERATE     : 6%
 * - AGGRESSIVE   : 8%
 */
@Service
@Transactional
public class RetirementScenarioService {

    private static final Map<ScenarioType, BigDecimal> ANNUAL_RETURN_RATES = Map.of(
            ScenarioType.CONSERVATIVE, new BigDecimal("0.04"),
            ScenarioType.MODERATE,     new BigDecimal("0.06"),
            ScenarioType.AGGRESSIVE,   new BigDecimal("0.08")
    );

    private final RetirementScenarioRepository scenarioRepository;
    private final RetirementGoalRepository     goalRepository;

    public RetirementScenarioService(RetirementScenarioRepository scenarioRepository,
                                     RetirementGoalRepository goalRepository) {
        this.scenarioRepository = scenarioRepository;
        this.goalRepository     = goalRepository;
    }

    /**
     * Runs a single scenario simulation for the given client and goal.
     *
     * @param clientId  the client ID
     * @param goalId    the retirement goal ID
     * @param request   the scenario input (type + monthly contribution)
     * @return ScenarioResponse with projected balance
     */
    public ScenarioResponse runScenario(Long clientId, Long goalId, ScenarioRequest request) {
        RetirementGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("RetirementGoal", goalId));

        RetirementScenario scenario = scenarioRepository
                .findByClientIdAndRetirementGoalIdAndScenarioType(clientId, goalId, request.getScenarioType())
                .orElse(new RetirementScenario());

        BigDecimal annualRate = ANNUAL_RETURN_RATES.get(request.getScenarioType());
        BigDecimal projected  = calculateProjectedBalance(goal, request.getMonthlyContribution(), annualRate);
        boolean achievable    = projected.compareTo(goal.getRequiredSavingsTarget()) >= 0;

        scenario.setClientId(clientId);
        scenario.setRetirementGoal(goal);
        scenario.setScenarioType(request.getScenarioType());
        scenario.setMonthlyContribution(request.getMonthlyContribution());
        scenario.setAnnualReturnRate(annualRate);
        scenario.setProjectedBalanceAtRetirement(projected);
        scenario.setIsGoalAchievable(achievable);

        RetirementScenario saved = scenarioRepository.save(scenario);
        return mapToResponse(saved);
    }

    /**
     * Runs all three scenario types (conservative, moderate, aggressive) side by side.
     *
     * @param clientId           the client ID
     * @param goalId             the retirement goal ID
     * @param monthlyContribution monthly contribution amount to compare across scenarios
     * @return ScenarioComparisonResponse with all three projections
     */
    public ScenarioComparisonResponse compareAllScenarios(Long clientId, Long goalId,
                                                          BigDecimal monthlyContribution) {
        List<ScenarioResponse> scenarios = Arrays.stream(ScenarioType.values())
                .map(type -> {
                    ScenarioRequest req = new ScenarioRequest();
                    req.setScenarioType(type);
                    req.setMonthlyContribution(monthlyContribution);
                    return runScenario(clientId, goalId, req);
                })
                .collect(Collectors.toList());

        ScenarioComparisonResponse comparison = new ScenarioComparisonResponse();
        comparison.setClientId(clientId);
        comparison.setGoalId(goalId);
        comparison.setScenarios(scenarios);
        return comparison;
    }

    /**
     * Calculates projected retirement balance using compound interest formula:
     * FV = PV*(1+r)^n + PMT * [((1+r)^n - 1) / r]
     * where r = monthly rate, n = months to retirement
     */
    private BigDecimal calculateProjectedBalance(RetirementGoal goal,
                                                  BigDecimal monthlyContribution,
                                                  BigDecimal annualRate) {
        int yearsToRetirement = goal.getTargetRetirementAge() - goal.getCurrentAge();
        int months            = yearsToRetirement * 12;
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        double r  = monthlyRate.doubleValue();
        int    n  = months;
        double pv = goal.getCurrentSavings().doubleValue();
        double pmt = monthlyContribution.doubleValue();

        double fv = pv * Math.pow(1 + r, n) + pmt * ((Math.pow(1 + r, n) - 1) / r);
        return BigDecimal.valueOf(fv).setScale(2, RoundingMode.HALF_UP);
    }

    private ScenarioResponse mapToResponse(RetirementScenario s) {
        ScenarioResponse resp = new ScenarioResponse();
        resp.setId(s.getId());
        resp.setClientId(s.getClientId());
        resp.setScenarioType(s.getScenarioType());
        resp.setMonthlyContribution(s.getMonthlyContribution());
        resp.setAnnualReturnRate(s.getAnnualReturnRate());
        resp.setProjectedBalanceAtRetirement(s.getProjectedBalanceAtRetirement());
        resp.setIsGoalAchievable(s.getIsGoalAchievable());
        return resp;
    }
}
