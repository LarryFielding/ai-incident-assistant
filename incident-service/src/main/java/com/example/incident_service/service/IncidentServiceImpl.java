package com.example.incident_service.service;

import com.example.incident_service.dto.CreateIncidentRequest;
import com.example.incident_service.dto.IncidentResponse;
import com.example.incident_service.dto.UpdateIncidentStatusRequest;
import com.example.incident_service.entity.Incident;
import com.example.incident_service.exception.ResourceNotFoundException;
import com.example.incident_service.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;

    @Override
    @Transactional
    public IncidentResponse createIncident(CreateIncidentRequest request) {
        Incident incident = Incident.builder()
                .title(request.title())
                .description(request.description())
                .rawLogs(request.rawLogs())
                .serviceName(request.serviceName())
                .environment(request.environment())
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

    private IncidentResponse mapToResponse(Incident incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getRawLogs(),
                incident.getServiceName(),
                incident.getEnvironment(),
                incident.getStatus(),
                incident.getCreatedAt(),
                incident.getUpdatedAt()
        );
    }
}
