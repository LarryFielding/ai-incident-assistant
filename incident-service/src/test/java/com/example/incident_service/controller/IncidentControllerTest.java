package com.example.incident_service.controller;

import com.example.incident_service.integration.ai.AiIncidentAnalysisResponse;
import com.example.incident_service.service.IncidentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class IncidentControllerTest {

    private MockMvc mockMvc;
    private IncidentService incidentService;

    @BeforeEach
    void setUp() {
        incidentService = Mockito.mock(IncidentService.class);
        IncidentController incidentController = new IncidentController(incidentService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(incidentController)
                .build();
    }

    @Test
    void analyzeIncident_shouldReturnAiAnalysisResponse() throws Exception {
        // Arrange
        Long incidentId = 35L;

        AiIncidentAnalysisResponse response = new AiIncidentAnalysisResponse(
                "The incident affecting payment-service was classified as DATABASE with HIGH severity.",
                "HIGH",
                "DATABASE",
                "The incident may be related to database connectivity or slow queries.",
                List.of(
                        "Review database connection pool metrics.",
                        "Check slow queries and database timeout logs."
                ),
                "Postmortem draft for payment-service incident."
        );

        when(incidentService.analyzeIncident(incidentId)).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/api/incidents/{id}/analysis", incidentId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.summary").value(response.summary()))
                .andExpect(jsonPath("$.severity").value("HIGH"))
                .andExpect(jsonPath("$.category").value("DATABASE"))
                .andExpect(jsonPath("$.possible_root_cause").value(response.possibleRootCause()))
                .andExpect(jsonPath("$.suggested_actions[0]").value("Review database connection pool metrics."))
                .andExpect(jsonPath("$.suggested_actions[1]").value("Check slow queries and database timeout logs."))
                .andExpect(jsonPath("$.postmortem_draft").value(response.postmortemDraft()));

        verify(incidentService).analyzeIncident(incidentId);
        verifyNoMoreInteractions(incidentService);
    }
}
