package com.example.incident_service.messaging.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IncidentAnalysisRequestedEvent(
        @JsonProperty("event_id")
        UUID eventId,

        @JsonProperty("event_type")
        String eventType,

        @JsonProperty("occurred_at")
        OffsetDateTime occurredAt,

        @JsonProperty("incident_id")
        Long incidentId,

        String title,

        String description,

        @JsonProperty("raw_logs")
        String rawLogs,

        @JsonProperty("service_name")
        String serviceName,

        String environment,

        @JsonProperty("environment_name")
        String environmentName,

        @JsonProperty("incident_occurred_at")
        OffsetDateTime incidentOccurredAt
) {
}
