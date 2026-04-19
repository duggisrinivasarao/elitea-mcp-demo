package com.edwardjones.advisor.service;

import com.edwardjones.advisor.dto.MeetingNoteRequest;
import com.edwardjones.advisor.model.ActionItem;
import com.edwardjones.advisor.model.MeetingNote;
import com.edwardjones.advisor.repository.ActionItemRepository;
import com.edwardjones.advisor.repository.MeetingNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MeetingNoteService.
 * Story: MAP-24 — Meeting Notes & Action Items
 * Covers all 3 Acceptance Criteria.
 */
class MeetingNoteServiceTest {

    @Mock private MeetingNoteRepository noteRepository;
    @Mock private ActionItemRepository actionItemRepository;
    @Mock private NotificationService notificationService;
    @InjectMocks private MeetingNoteService noteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * AC1: Given meeting is completed, When advisor publishes notes,
     * Then client can access them from portal.
     */
    @Test
    @DisplayName("MAP-24-AC1: publishing notes marks them as published and notifies client")
    void publishNotes_shouldSetPublishedAndNotify() {
        MeetingNoteRequest request = new MeetingNoteRequest();
        request.setMeetingId(1L);
        request.setAdvisorId(2L);
        request.setClientId(3L);
        request.setNoteContent("Discussed diversification strategy.");
        request.setActionItems(List.of());

        MeetingNote saved = MeetingNote.builder()
                .id(10L).meetingId(1L).advisorId(2L).clientId(3L)
                .noteContent("Discussed diversification strategy.")
                .published(true).publishedAt(LocalDateTime.now())
                .build();

        when(noteRepository.save(any())).thenReturn(saved);

        MeetingNote result = noteService.publishNotes(request);

        assertThat(result.isPublished()).isTrue();
        assertThat(result.getPublishedAt()).isNotNull();
        verify(notificationService).sendNotePublishedNotification(3L, 1L);
    }

    /**
     * AC2: Given notes are available, When client views them,
     * Then action items are listed with due dates and status.
     */
    @Test
    @DisplayName("MAP-24-AC2: action items are saved with status and due dates")
    void publishNotes_withActionItems_shouldSaveAllItems() {
        MeetingNoteRequest.ActionItemRequest ai = new MeetingNoteRequest.ActionItemRequest();
        ai.setDescription("Increase 401k contribution");
        ai.setDueDate(LocalDate.now().plusDays(30));

        MeetingNoteRequest request = new MeetingNoteRequest();
        request.setMeetingId(1L);
        request.setAdvisorId(2L);
        request.setClientId(3L);
        request.setNoteContent("Review savings rate.");
        request.setActionItems(List.of(ai));

        MeetingNote saved = MeetingNote.builder()
                .id(10L).meetingId(1L).advisorId(2L).clientId(3L)
                .noteContent("Review savings rate.")
                .published(true).publishedAt(LocalDateTime.now())
                .build();

        when(noteRepository.save(any())).thenReturn(saved);

        noteService.publishNotes(request);

        verify(actionItemRepository).saveAll(argThat(items -> {
            List<ActionItem> list = (List<ActionItem>) items;
            return list.size() == 1 &&
                    list.get(0).getStatus() == ActionItem.ActionStatus.PENDING &&
                    list.get(0).getDueDate().equals(ai.getDueDate());
        }));
    }

    /**
     * AC3: Given an action item is updated,
     * Then its status is persisted correctly.
     */
    @Test
    @DisplayName("MAP-24-AC3: updating action item status saves new status")
    void updateActionItemStatus_shouldPersistNewStatus() {
        ActionItem item = ActionItem.builder()
                .id(20L).description("Review portfolio").dueDate(LocalDate.now().plusDays(7))
                .status(ActionItem.ActionStatus.PENDING).build();

        when(actionItemRepository.findById(20L)).thenReturn(Optional.of(item));
        when(actionItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ActionItem updated = noteService.updateActionItemStatus(20L, ActionItem.ActionStatus.COMPLETED);

        assertThat(updated.getStatus()).isEqualTo(ActionItem.ActionStatus.COMPLETED);
    }

    /**
     * Edge: Client notes retrieval returns only published notes.
     */
    @Test
    @DisplayName("MAP-24-edge: getClientNotes returns only published notes")
    void getClientNotes_shouldReturnOnlyPublished() {
        MeetingNote note = MeetingNote.builder().id(1L).clientId(3L).published(true).build();
        when(noteRepository.findByClientIdAndPublishedTrue(3L)).thenReturn(List.of(note));

        List<MeetingNote> result = noteService.getClientNotes(3L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isPublished()).isTrue();
    }
}
