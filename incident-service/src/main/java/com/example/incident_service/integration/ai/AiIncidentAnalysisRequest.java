package com.example.incident_service.integration.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record AiIncidentAnalysisRequest(

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