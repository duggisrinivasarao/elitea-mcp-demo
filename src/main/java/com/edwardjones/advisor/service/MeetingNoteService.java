package com.edwardjones.advisor.service;

import com.edwardjones.advisor.dto.MeetingNoteRequest;
import com.edwardjones.advisor.model.ActionItem;
import com.edwardjones.advisor.model.MeetingNote;
import com.edwardjones.advisor.repository.ActionItemRepository;
import com.edwardjones.advisor.repository.MeetingNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service handling meeting note creation, publishing, and action item tracking.
 * Story: MAP-24 — Meeting Notes & Action Items
 */
@Service
@RequiredArgsConstructor
public class MeetingNoteService {

    private final MeetingNoteRepository noteRepository;
    private final ActionItemRepository actionItemRepository;
    private final NotificationService notificationService;

    /**
     * Creates and publishes meeting notes with action items.
     * AC: After meeting completion, advisor publishes notes accessible by client.
     * AC: Action items listed with due dates and status.
     *
     * @param request the note creation request
     * @return saved MeetingNote entity
     */
    @Transactional
    public MeetingNote publishNotes(MeetingNoteRequest request) {
        MeetingNote note = MeetingNote.builder()
                .meetingId(request.getMeetingId())
                .advisorId(request.getAdvisorId())
                .clientId(request.getClientId())
                .noteContent(request.getNoteContent())
                .published(true)
                .publishedAt(LocalDateTime.now())
                .build();

        MeetingNote saved = noteRepository.save(note);

        if (request.getActionItems() != null) {
            List<ActionItem> items = request.getActionItems().stream()
                    .map(ai -> ActionItem.builder()
                            .meetingNote(saved)
                            .description(ai.getDescription())
                            .dueDate(ai.getDueDate())
                            .status(ActionItem.ActionStatus.PENDING)
                            .build())
                    .collect(Collectors.toList());
            actionItemRepository.saveAll(items);
        }

        notificationService.sendNotePublishedNotification(saved.getClientId(), saved.getMeetingId());
        return saved;
    }

    /**
     * Retrieves all published notes for a client.
     * AC: Client can access notes from portal.
     *
     * @param clientId the client's ID
     * @return list of published MeetingNotes
     */
    public List<MeetingNote> getClientNotes(Long clientId) {
        return noteRepository.findByClientIdAndPublishedTrue(clientId);
    }

    /**
     * Updates the status of a specific action item.
     * AC: Action items show status (pending/completed).
     *
     * @param actionItemId the ID of the action item
     * @param newStatus    the updated status
     * @return updated ActionItem
     */
    @Transactional
    public ActionItem updateActionItemStatus(Long actionItemId, ActionItem.ActionStatus newStatus) {
        ActionItem item = actionItemRepository.findById(actionItemId)
                .orElseThrow(() -> new IllegalArgumentException("Action item not found: " + actionItemId));
        item.setStatus(newStatus);
        return actionItemRepository.save(item);
    }
}
