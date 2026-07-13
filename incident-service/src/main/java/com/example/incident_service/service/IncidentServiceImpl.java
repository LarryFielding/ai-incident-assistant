package com.example.incident_service.service;

import com.example.incident_service.dto.CreateIncidentRequest;
import com.example.incident_service.dto.IncidentResponse;
import com.example.incident_service.dto.UpdateIncidentStatusRequest;
import com.example.incident_service.entity.Incident;
import com.example.incident_service.entity.IncidentAnalysis;
import com.example.incident_service.exception.ResourceNotFoundException;
import com.example.incident_service.integration.ai.AiAnalysisClient;
import com.example.incident_service.integration.ai.AiIncidentAnalysisRequest;
import com.example.incident_service.integration.ai.AiIncidentAnalysisResponse;
import com.example.incident_service.messaging.KafkaTopics;
import com.example.incident_service.messaging.event.IncidentAnalysisRequestedEvent;
import com.example.incident_service.messaging.producer.IncidentAnalysisRequestedProducer;
import com.example.incident_service.repository.IncidentAnalysisRepository;
import com.example.incident_service.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final AiAnalysisClient aiAnalysisClient;
    private final IncidentAnalysisRepository incidentAnalysisRepository;
    private final IncidentAnalysisRequestedProducer analysisRequestedProducer;

    @Override
    @Transactional
    public IncidentResponse createIncident(CreateIncidentRequest request) {

        OffsetDateTime incidentOccurredAt =
                request.incidentOccurredAt() == null ?
                        OffsetDateTime.now() :
                        request.incidentOccurredAt();

        Incident incident = Incident.builder()
                .title(request.title())
                .description(request.description())
                .rawLogs(request.rawLogs())
                .serviceName(request.serviceName())
                .environment(request.environment())
                .environmentName(request.environmentName())
                .incidentOccurredAt(incidentOccurredAt)
                .build();

        Incident saved = incidentRepository.save(incident);
        return mapToResponse(saved);
    }

    @Override
    public List<IncidentResponse> getAllIncidents() {
        return incidentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public IncidentResponse getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));

        return mapToResponse(incident);
    }

    @Override
    @Transactional
    public IncidentResponse updateIncidentStatus(Long id, UpdateIncidentStatusRequest request) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));

        incident.setStatus(request.status());

        Incident updated = incidentRepository.save(incident);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public AiIncidentAnalysisResponse analyzeIncident(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));

        AiIncidentAnalysisRequest request = new AiIncidentAnalysisRequest(
                incident.getTitle(),
                incident.getDescription(),
                incident.getRawLogs(),
                incident.getServiceName(),
                incident.getEnvironment(),
                incident.getEnvironment(),
                incident.getCreatedAt()
        );
        log.debug("Sending incident analysis request to ai-service: {}", request);

        AiIncidentAnalysisResponse response = aiAnalysisClient.analyzeIncident(request);

        String suggestedActions = response.suggestedActions() == null
                ? ""
                : String.join(System.lineSeparator(), response.suggestedActions());

        IncidentAnalysis analysis = IncidentAnalysis.builder()
                .incident(incident)
                .summary(response.summary())
                .severity(response.severity())
                .category(response.category())
                .possibleRootCause(response.possibleRootCause())
                .suggestedActions(suggestedActions)
                .postmortemDraft(response.postmortemDraft())
                .build();

        incidentAnalysisRepository.save(analysis);

        log.info("Saved incident analysis ID {} for incident ID {}", analysis.getId(), id);

        return response;
    }

    private IncidentResponse mapToResponse(Incident incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getRawLogs(),
                incident.getServiceName(),
                incident.getEnvironment(),
                incident.getEnvironmentName(),
                incident.getStatus(),
                incident.getCreatedAt(),
                incident.getUpdatedAt(),
                incident.getIncidentOccurredAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public void requestIncidentAnalysis(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Incident not found with id: " + incidentId
                        )
                );

        IncidentAnalysisRequestedEvent event =
                new IncidentAnalysisRequestedEvent(
                        UUID.randomUUID(),
                        KafkaTopics.INCIDENT_ANALYSIS_REQUESTED,
                        OffsetDateTime.now(),
                        incident.getId(),
                        incident.getTitle(),
                        incident.getDescription(),
                        incident.getRawLogs(),
                        incident.getServiceName(),
                        incident.getEnvironment(),
                        incident.getEnvironmentName(),
                        incident.getIncidentOccurredAt()
                );

        analysisRequestedProducer.publish(event);
    }
}
