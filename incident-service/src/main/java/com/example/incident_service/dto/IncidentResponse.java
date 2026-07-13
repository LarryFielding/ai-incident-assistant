package com.example.incident_service.dto;

import com.example.incident_service.entity.IncidentStatus;

import java.time.OffsetDateTime;

public record IncidentResponse(
        Long id,
        String title,
        String description,
        String rawLogs,
        String serviceName,
        String environment,
        String environmentName,
        IncidentStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime incidentOccurredAt
) {
}