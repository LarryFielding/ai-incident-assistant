package com.example.incident_service.service;

import com.example.incident_service.entity.Incident;
import com.example.incident_service.entity.IncidentAnalysis;
import com.example.incident_service.integration.ai.AiAnalysisClient;
import com.example.incident_service.integration.ai.AiIncidentAnalysisRequest;
import com.example.incident_service.integration.ai.AiIncidentAnalysisResponse;
import com.example.incident_service.repository.IncidentAnalysisRepository;
import com.example.incident_service.repository.IncidentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IncidentServiceImplTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private AiAnalysisClient aiAnalysisClient;

    @Mock
    private IncidentAnalysisRepository incidentAnalysisRepository;

    @InjectMocks
    private IncidentServiceImpl incidentService;

    @Test
    void analyzeIncident_shouldCallAiServiceAndPersistAnalysis() {
        // Arrange
        Long incidentId = 35L;
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-07-09T23:00:00Z");

        Incident incident = Incident.builder()
                .id(incidentId)
                .title("Database timeout in payment service")
                .description("Users report intermittent failures during checkout.")
                .rawLogs("java.sql.SQLTimeoutException: query timed out")
                .serviceName("payment-service")
                .environment("PROD")
                .createdAt(createdAt)
                .build();

        AiIncidentAnalysisResponse aiResponse = new AiIncidentAnalysisResponse(
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

        when(incidentRepository.findById(incidentId)).thenReturn(Optional.of(incident));
        when(aiAnalysisClient.analyzeIncident(any(AiIncidentAnalysisRequest.class))).thenReturn(aiResponse);
        when(incidentAnalysisRepository.save(any(IncidentAnalysis.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AiIncidentAnalysisResponse result = incidentService.analyzeIncident(incidentId);

        // Assert
        assertThat(result).isEqualTo(aiResponse);

        ArgumentCaptor<AiIncidentAnalysisRequest> requestCaptor =
                ArgumentCaptor.forClass(AiIncidentAnalysisRequest.class);

        verify(aiAnalysisClient).analyzeIncident(requestCaptor.capture());

        AiIncidentAnalysisRequest sentRequest = requestCaptor.getValue();

        assertThat(sentRequest.title()).isEqualTo("Database timeout in payment service");
        assertThat(sentRequest.description()).isEqualTo("Users report intermittent failures during checkout.");
        assertThat(sentRequest.rawLogs()).isEqualTo("java.sql.SQLTimeoutException: query timed out");
        assertThat(sentRequest.serviceName()).isEqualTo("payment-service");
        assertThat(sentRequest.environment()).isEqualTo("PROD");
        assertThat(sentRequest.environmentName()).isEqualTo("PROD");
        assertThat(sentRequest.incidentOccurredAt()).isEqualTo(createdAt);

        ArgumentCaptor<IncidentAnalysis> analysisCaptor =
                ArgumentCaptor.forClass(IncidentAnalysis.class);

        verify(incidentAnalysisRepository).save(analysisCaptor.capture());

        IncidentAnalysis savedAnalysis = analysisCaptor.getValue();

        assertThat(savedAnalysis.getIncident()).isEqualTo(incident);
        assertThat(savedAnalysis.getSummary()).isEqualTo(aiResponse.summary());
        assertThat(savedAnalysis.getSeverity()).isEqualTo("HIGH");
        assertThat(savedAnalysis.getCategory()).isEqualTo("DATABASE");
        assertThat(savedAnalysis.getPossibleRootCause()).isEqualTo(aiResponse.possibleRootCause());
        assertThat(savedAnalysis.getSuggestedActions())
                .contains("Review database connection pool metrics.")
                .contains("Check slow queries and database timeout logs.");
        assertThat(savedAnalysis.getPostmortemDraft()).isEqualTo(aiResponse.postmortemDraft());

        verify(incidentRepository).findById(incidentId);
        verifyNoMoreInteractions(incidentRepository, aiAnalysisClient, incidentAnalysisRepository);
    }

}
