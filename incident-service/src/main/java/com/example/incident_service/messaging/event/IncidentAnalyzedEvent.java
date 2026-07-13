package com.example.incident_service.messaging.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record IncidentAnalyzedEvent(
        @JsonProperty("event_id")
        UUID eventId,

        @JsonProperty("event_type")
        String eventType,

        @JsonProperty("occurred_at")
        OffsetDateTime occurredAt,

        @JsonProperty("incident_id")
        Long incidentId,

        String summary,

        String severity,

        String category,

        @JsonProperty("possible_root_cause")
        String possibleRootCause,

        @JsonProperty("suggested_actions")
        List<String> suggestedActions,

        @JsonProperty("postmortem_draft")
        String postmortemDraft
) {
}
