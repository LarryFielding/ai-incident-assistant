package com.example.incident_service.dto;

import com.example.incident_service.entity.IncidentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateIncidentStatusRequest(
        @NotNull
        IncidentStatus status
) {
}
