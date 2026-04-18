# Epic 4 — Portfolio Dashboard & Investment Tracking

> **Jira Epic:** MAP-4  
> **Branch:** `feature/epic4-portfolio-dashboard`  
> **Status:** ✅ Implemented

---

## 📋 Overview

This module delivers the real-time portfolio dashboard and investment tracking capabilities
for both clients and financial advisors. It covers holdings visibility, performance charts,
asset allocation breakdowns, advisor-wide portfolio views, and portfolio alert notifications.

---

## 📦 Module Structure

```
epic4-portfolio-dashboard/
├── src/main/java/com/edwardjones/portfolio/
│   ├── model/
│   │   ├── Holding.java                   # MAP-13: Investment holding entity
│   │   ├── PerformanceSnapshot.java       # MAP-14: Daily portfolio snapshot
│   │   ├── AssetAllocation.java           # MAP-15: Target allocation config
│   │   └── PortfolioAlert.java            # MAP-17: Alert threshold entity
│   ├── repository/
│   │   ├── HoldingRepository.java
│   │   ├── PerformanceSnapshotRepository.java
│   │   ├── AssetAllocationRepository.java
│   │   └── PortfolioAlertRepository.java
│   ├── service/
│   │   ├── PortfolioDashboardService.java  # MAP-13
│   │   ├── PerformanceChartService.java    # MAP-14
│   │   ├── AssetAllocationService.java     # MAP-15
│   │   ├── AdvisorDashboardService.java    # MAP-16
│   │   ├── PortfolioAlertService.java      # MAP-17
│   │   └── NotificationService.java        # MAP-17: Email/Push stub
│   ├── controller/
│   │   ├── PortfolioDashboardController.java
│   │   ├── PerformanceChartController.java
│   │   ├── AssetAllocationController.java
│   │   ├── AdvisorDashboardController.java
│   │   └── PortfolioAlertController.java
│   └── dto/
│       ├── PortfolioDashboardResponse.java
│       ├── PerformanceChartResponse.java
│       ├── AssetAllocationResponse.java
│       ├── AdvisorDashboardResponse.java
│       └── PortfolioAlertRequest.java
└── src/test/java/com/edwardjones/portfolio/service/
    ├── PortfolioDashboardServiceTest.java  # 5 test cases
    ├── PerformanceChartServiceTest.java    # 5 test cases
    ├── AssetAllocationServiceTest.java     # 4 test cases
    ├── AdvisorDashboardServiceTest.java    # 5 test cases
    └── PortfolioAlertServiceTest.java      # 6 test cases
```

---

## 🗺️ User Stories Implemented

| Story  | Summary                                          | Status |
|--------|--------------------------------------------------|--------|
| MAP-13 | Real-time Portfolio Holdings Dashboard           | ✅ Done |
| MAP-14 | Performance Charts over Time                     | ✅ Done |
| MAP-15 | Asset Allocation Breakdown                       | ✅ Done |
| MAP-16 | Advisor Consolidated Portfolio Dashboard         | ✅ Done |
| MAP-17 | Portfolio Alert Notifications                    | ✅ Done |

---

## 🌐 API Endpoints

| Method | Path                                          | Story  | Description                        |
|--------|-----------------------------------------------|--------|------------------------------------|
| GET    | `/api/portfolio/{clientId}/dashboard`         | MAP-13 | Real-time holdings + daily P&L     |
| GET    | `/api/portfolio/{clientId}/performance`       | MAP-14 | Performance chart (1M/3M/6M/1Y/ALL)|
| GET    | `/api/portfolio/{clientId}/allocation`        | MAP-15 | Asset allocation actual vs. target |
| GET    | `/api/advisor/{advisorId}/dashboard`          | MAP-16 | Consolidated advisor view          |
| POST   | `/api/portfolio/{clientId}/alerts`            | MAP-17 | Configure alert threshold          |
| GET    | `/api/portfolio/{clientId}/alerts`            | MAP-17 | List all alerts                    |
| PUT    | `/api/portfolio/{clientId}/alerts/disable`    | MAP-17 | Disable all alerts                 |
| PUT    | `/api/portfolio/{clientId}/alerts/enable`     | MAP-17 | Re-enable all alerts               |

---

## ✅ Test Coverage Summary

| Test Class                      | Test Cases | Acceptance Criteria Covered                              |
|---------------------------------|-----------|----------------------------------------------------------|
| PortfolioDashboardServiceTest   | 5         | Holdings display, total P&L, stale data, validation      |
| PerformanceChartServiceTest     | 5         | Time ranges, tooltip data, insufficient data, ALL range  |
| AssetAllocationServiceTest      | 4         | Percentages, holdings per segment, actual vs target      |
| AdvisorDashboardServiceTest     | 5         | Client listing, sort, flagging, empty, validation        |
| PortfolioAlertServiceTest       | 6         | Configure, threshold breach, disable, enable, validation |

**Total: 25 test cases**

---

## 🏗️ Tech Stack

- **Java 17** + **Spring Boot 3**
- **Spring Data JPA** + **Hibernate**
- **Jakarta Validation**
- **JUnit 5** + **Mockito** + **AssertJ**
