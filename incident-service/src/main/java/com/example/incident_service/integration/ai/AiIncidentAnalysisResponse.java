package com.example.incident_service.integration.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiIncidentAnalysisResponse(
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
