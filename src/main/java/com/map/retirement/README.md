# MAP-5 — Retirement Planning Tools

> Epic: Retirement Planning Tools  
> Status: ✅ Implemented  
> Branch: `feature/epic5-retirement-planning`

---

## 📖 Overview

This module implements all retirement planning features for the financial portal. It enables clients to define retirement goals, run scenario simulations, track IRA and 401(k) contribution progress, and allows financial advisors to generate comprehensive retirement readiness reports.

---

## 📦 Module Structure

```
com.map.retirement
├── controller
│   ├── RetirementGoalController.java         # MAP-18
│   ├── RetirementScenarioController.java      # MAP-19
│   ├── RetirementReportController.java        # MAP-20
│   └── ContributionTrackerController.java     # MAP-21
├── service
│   ├── RetirementGoalService.java             # MAP-18
│   ├── RetirementScenarioService.java         # MAP-19
│   ├── RetirementReportService.java           # MAP-20
│   ├── ContributionTrackerService.java        # MAP-21
│   └── NotificationService.java              # MAP-21 notifications
├── repository
│   ├── RetirementGoalRepository.java
│   ├── RetirementScenarioRepository.java
│   ├── RetirementReportRepository.java
│   └── ContributionTrackerRepository.java
├── model
│   ├── RetirementGoal.java
│   ├── RetirementScenario.java
│   ├── RetirementReport.java
│   └── ContributionTracker.java
├── dto
│   ├── RetirementGoalRequest / Response
│   ├── ScenarioRequest / Response / ComparisonResponse
│   ├── ContributionTrackerRequest / Response
│   └── RetirementReportResponse
└── exception
    ├── InvalidRetirementGoalException.java
    └── ResourceNotFoundException.java
```

---

## 🗂️ User Stories

### MAP-18 — Retirement Goal & Savings Calculator
**As a client**, I want to input my target retirement age and income needs, so the system can calculate how much I need to save.

| AC | Test |
|----|------|
| Valid inputs → required savings target displayed | `RetirementGoalServiceTest#givenValidInputs_whenSaveRetirementGoal_thenRequiredTargetIsCalculated` |
| Valid inputs → projected monthly savings returned | `RetirementGoalServiceTest#givenValidInputs_whenSaveRetirementGoal_thenProjectedMonthlySavingsReturned` |
| Retirement age ≤ current age → validation error | `RetirementGoalServiceTest#givenRetirementAgeEqualsCurrentAge_whenSaveGoal_thenThrowsException` |

**Endpoints:**
- `POST /api/v1/clients/{clientId}/retirement/goals`
- `GET  /api/v1/clients/{clientId}/retirement/goals`

---

### MAP-19 — Retirement Scenario Simulation
**As a client**, I want to run retirement scenarios, so I can see the projected impact on my retirement savings.

| AC | Test |
|----|------|
| Scenario type selected → projected balance chart | `RetirementScenarioServiceTest#givenConservativeScenario_*` |
| All three scenarios shown side-by-side | `RetirementScenarioServiceTest#givenCompareRequest_whenCompareAllScenarios_*` |
| Contribution adjusted → projections update dynamically | `RetirementScenarioServiceTest#givenHigherContribution_whenRunScenario_*` |

**Scenarios & Return Rates:**
| Type | Annual Return |
|------|--------------|
| Conservative | 4% |
| Moderate | 6% |
| Aggressive | 8% |

**Endpoints:**
- `POST /api/v1/clients/{clientId}/retirement/goals/{goalId}/scenarios`
- `GET  /api/v1/clients/{clientId}/retirement/goals/{goalId}/scenarios/compare?monthlyContribution=X`

---

### MAP-20 — Retirement Readiness Report
**As a financial advisor**, I want to generate a retirement readiness report, so I can present it during advisory meetings.

| AC | Test |
|----|------|
| Advisor generates report → PDF with full data | `RetirementReportServiceTest#givenCompleteClientData_*` |
| Report is branded and downloadable | `RetirementReportServiceTest#givenCompleteData_whenGenerateReport_thenReportUrlPresent` |
| Incomplete data → missing fields clearly marked | `RetirementReportServiceTest#givenNoRetirementGoal_*` |

**Endpoints:**
- `POST /api/v1/advisors/{advisorId}/clients/{clientId}/retirement/reports`
- `GET  /api/v1/advisors/{advisorId}/clients/{clientId}/retirement/reports`

---

### MAP-21 — IRA/401(k) Contribution Tracker
**As a client**, I want to view my IRA and 401(k) contribution progress, so I can maximize my tax-advantaged savings.

| AC | Test |
|----|------|
| YTD contributions shown as % of IRS limit | `ContributionTrackerServiceTest#givenLinkedAccount_whenUpdateContribution_*` |
| 80% threshold reached → notification sent | `ContributionTrackerServiceTest#givenContributionsAt80Percent_*` |
| New year → IRS limits auto-updated | `ContributionTrackerServiceTest#givenNewIrsLimit_whenUpdateAnnualLimits_*` |

**Default IRS Limits (2024):**
| Account Type | Annual Limit |
|-------------|-------------|
| IRA / Roth IRA | $7,000 |
| 401(k) / Roth 401(k) | $23,000 |

**Endpoints:**
- `GET   /api/v1/clients/{clientId}/retirement/contributions`
- `PUT   /api/v1/clients/{clientId}/retirement/contributions`
- `PATCH /api/v1/retirement/contributions/irs-limits`

---

## 🧪 Test Summary

| Test Class | Tests | ACs Covered |
|-----------|-------|------------|
| `RetirementGoalServiceTest` | 5 | MAP-18 AC1, AC2, AC3 |
| `RetirementScenarioServiceTest` | 5 | MAP-19 AC1, AC2, AC3 |
| `ContributionTrackerServiceTest` | 6 | MAP-21 AC1, AC2, AC3 |
| `RetirementReportServiceTest` | 5 | MAP-20 AC1, AC2, AC3 |
| **Total** | **21** | **12 ACs** |

---

## ⚙️ Tech Stack
- Java 17 + Spring Boot 3
- Spring Data JPA + Hibernate
- Jakarta Validation
- JUnit 5 + Mockito + AssertJ
