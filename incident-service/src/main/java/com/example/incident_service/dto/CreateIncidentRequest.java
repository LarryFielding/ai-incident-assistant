package com.example.incident_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record CreateIncidentRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 200)
        String title,

        @NotBlank(message = "Description is required")
        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        @JsonProperty("raw_logs")
        @Size(max = 20000, message = "Raw logs must not exceed 20000 characters")
        String rawLogs,

        @JsonProperty("service_name")
        @NotBlank(message = "Service name is required")
        @Size(max = 100)
        String serviceName,

        @NotBlank(message = "Environment is required")
        @Size(max = 50)
        String environment,

        @JsonProperty("environment_name")
        String environmentName,

        @JsonProperty("incident_occurred_at")
        OffsetDateTime incidentOccurredAt
) {
}