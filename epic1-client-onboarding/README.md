# Epic 1: Client Onboarding & Profile Management

## Overview
This module handles the complete client onboarding lifecycle for Edward Jones.
It covers client registration, risk assessment, advisor assignment, document upload, and financial goal setting.

## Stories Covered
- MAP-8: Register and complete financial profile
- MAP-9: Risk tolerance questionnaire
- MAP-10: Automatic advisor assignment
- MAP-11: Secure document upload & KYC
- MAP-12: Set financial goals

## Module Structure
```
epic1-client-onboarding/
├── model/
│   ├── Client.java
│   ├── RiskTolerance.java
│   ├── FinancialGoal.java
│   └── ClientDocument.java
├── repository/
│   ├── ClientRepository.java
│   ├── RiskToleranceRepository.java
│   ├── FinancialGoalRepository.java
│   └── ClientDocumentRepository.java
├── service/
│   ├── ClientOnboardingService.java
│   ├── RiskToleranceService.java
│   ├── AdvisorAssignmentService.java
│   ├── DocumentUploadService.java
│   └── FinancialGoalService.java
├── controller/
│   ├── ClientOnboardingController.java
│   └── FinancialGoalController.java
├── dto/
│   ├── ClientRegistrationRequest.java
│   ├── ClientRegistrationResponse.java
│   ├── RiskToleranceRequest.java
│   ├── FinancialGoalRequest.java
│   └── DocumentUploadResponse.java
└── test/
    ├── ClientOnboardingServiceTest.java
    ├── RiskToleranceServiceTest.java
    ├── AdvisorAssignmentServiceTest.java
    ├── DocumentUploadServiceTest.java
    └── FinancialGoalServiceTest.java
```

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Maven

## API Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/v1/clients/register | Register a new client |
| POST | /api/v1/clients/{id}/risk-tolerance | Submit risk tolerance questionnaire |
| GET  | /api/v1/clients/{id}/advisor | Get assigned advisor |
| POST | /api/v1/clients/{id}/documents | Upload KYC documents |
| POST | /api/v1/clients/{id}/goals | Set financial goals |
| GET  | /api/v1/clients/{id}/goals | Get client financial goals |
