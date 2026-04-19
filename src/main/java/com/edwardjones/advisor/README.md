# MAP-6 — Financial Advisor Collaboration & Communication

## Overview
This module implements all collaboration and communication features between clients and their
dedicated financial advisors on the Edward Jones client portal.

---

## User Stories Implemented

| Story   | Title                                  | Status |
|---------|----------------------------------------|--------|
| MAP-22  | Meeting Scheduling                     | ✅ Done |
| MAP-23  | Secure Advisor Messaging               | ✅ Done |
| MAP-24  | Meeting Notes & Action Items           | ✅ Done |
| MAP-25  | Account Flagging for Priority Review   | ✅ Done |
| MAP-26  | Automated Meeting Reminders            | ✅ Done |

---

## Module Structure

```
com.edwardjones.advisor/
├── model/
│   ├── Meeting.java               # MAP-22
│   ├── SecureMessage.java         # MAP-23
│   ├── MeetingNote.java           # MAP-24
│   ├── ActionItem.java            # MAP-24
│   ├── AccountFlag.java           # MAP-25
│   └── MeetingReminder.java       # MAP-26
├── repository/
│   ├── MeetingRepository.java
│   ├── SecureMessageRepository.java
│   ├── MeetingNoteRepository.java
│   ├── ActionItemRepository.java
│   ├── AccountFlagRepository.java
│   └── MeetingReminderRepository.java
├── service/
│   ├── MeetingService.java
│   ├── SecureMessageService.java
│   ├── MeetingNoteService.java
│   ├── AccountFlagService.java
│   ├── MeetingReminderService.java
│   └── NotificationService.java
├── controller/
│   ├── MeetingController.java
│   ├── SecureMessageController.java
│   ├── MeetingNoteController.java
│   ├── AccountFlagController.java
│   └── MeetingReminderController.java
└── dto/
    ├── MeetingRequest.java / MeetingResponse.java
    ├── SecureMessageRequest.java / SecureMessageResponse.java
    ├── MeetingNoteRequest.java
    ├── AccountFlagRequest.java
    └── ReminderPreferenceRequest.java
```

---

## API Endpoints

### MAP-22 — Meeting Scheduling
| Method   | Endpoint                                    | Description                        |
|----------|---------------------------------------------|------------------------------------|
| `POST`   | `/api/meetings`                             | Book a new meeting                 |
| `DELETE` | `/api/meetings/{meetingId}`                 | Cancel a meeting (24h notice req.) |
| `PATCH`  | `/api/meetings/{meetingId}/reschedule`      | Reschedule a meeting               |
| `GET`    | `/api/meetings/advisor/{advisorId}/availability` | View advisor availability     |

### MAP-23 — Secure Messaging
| Method  | Endpoint                          | Description                        |
|---------|-----------------------------------|------------------------------------|
| `POST`  | `/api/messages`                   | Send a secure encrypted message    |
| `PATCH` | `/api/messages/{messageId}/read`  | Mark message as read               |
| `POST`  | `/api/messages/{messageId}/breach`| Report a security breach           |
| `GET`   | `/api/messages/unread`            | Get unread messages by recipient   |

### MAP-24 — Meeting Notes & Action Items
| Method  | Endpoint                                          | Description                    |
|---------|---------------------------------------------------|--------------------------------|
| `POST`  | `/api/meeting-notes`                              | Publish meeting notes          |
| `GET`   | `/api/meeting-notes/client/{clientId}`            | Get client's published notes   |
| `PATCH` | `/api/meeting-notes/action-items/{id}/status`     | Update action item status      |

### MAP-25 — Account Flagging
| Method   | Endpoint                              | Description                        |
|----------|---------------------------------------|------------------------------------|
| `POST`   | `/api/account-flags`                  | Flag account for priority review   |
| `DELETE` | `/api/account-flags/{clientId}`       | Remove priority flag               |
| `GET`    | `/api/account-flags/advisor/{id}`     | Get all flagged accounts           |
| `GET`    | `/api/account-flags/{clientId}/status`| Check flag status                  |

### MAP-26 — Automated Meeting Reminders
| Method  | Endpoint                           | Description                        |
|---------|------------------------------------|------------------------------------|
| `POST`  | `/api/reminders/schedule`          | Schedule a meeting reminder        |
| `POST`  | `/api/reminders/opt-out`           | Opt client out of reminders        |
| `GET`   | `/api/reminders/meeting/{id}`      | Get all reminders for a meeting    |

---

## Key Design Decisions
- **Encryption**: Messages use Base64 as a placeholder; replace with AES-256/GCM in production
- **Scheduled Reminders**: `@Scheduled(cron)` runs hourly — externalize to Quartz for production scale
- **Opt-out**: Respected at both schedule time and dispatch time
- **Cancellation Policy**: 24-hour minimum enforced in service layer
- **Flagging**: One flag record per client — upsert pattern used

---

## Tech Stack
- Java 17 + Spring Boot 3
- Spring Data JPA + Hibernate
- Jakarta Validation
- Lombok
- JUnit 5 + Mockito
