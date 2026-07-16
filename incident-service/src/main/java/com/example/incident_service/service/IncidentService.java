package com.example.incident_service.service;

import com.example.incident_service.dto.CreateIncidentRequest;
import com.example.incident_service.dto.IncidentResponse;
import com.example.incident_service.dto.UpdateIncidentStatusRequest;
import com.example.incident_service.integration.ai.AiIncidentAnalysisResponse;
import com.example.incident_service.messaging.event.IncidentAnalyzedEvent;

import java.util.List;

public interface IncidentService {

    IncidentResponse createIncident(CreateIncidentRequest request);

    List<IncidentResponse> getAllIncidents();

    IncidentResponse getIncidentById(Long id);

    IncidentResponse updateIncidentStatus(Long id, UpdateIncidentStatusRequest request);

    AiIncidentAnalysisResponse analyzeIncident(Long id);

    void requestIncidentAnalysis(Long incidentId);

    void saveIncidentAnalysis(IncidentAnalyzedEvent event);
}
