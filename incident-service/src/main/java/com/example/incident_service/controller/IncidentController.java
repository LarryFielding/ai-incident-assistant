package com.example.incident_service.controller;

import com.example.incident_service.dto.CreateIncidentRequest;
import com.example.incident_service.dto.IncidentResponse;
import com.example.incident_service.dto.UpdateIncidentStatusRequest;
import com.example.incident_service.integration.ai.AiIncidentAnalysisResponse;
import com.example.incident_service.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@Slf4j
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    public ResponseEntity<Void> createIncident(@Valid @RequestBody CreateIncidentRequest request) {
        log.info("Received request to create incident: {}", request.title());
        IncidentResponse newIncident = incidentService.createIncident(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newIncident.id())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<IncidentResponse>> getAllIncidents() {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncidentById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IncidentResponse> updateIncidentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateIncidentStatusRequest request
    ) {
        log.info("Received request to update status for incident ID {}: {}", id, request);
        return ResponseEntity.ok(incidentService.updateIncidentStatus(id, request));
    }

    @PostMapping("/{id}/analysis")
    public ResponseEntity<AiIncidentAnalysisResponse> analyzeIncident(@PathVariable Long id) {
        log.info("Received request to analyze incident ID {}", id);
        return ResponseEntity.ok(incidentService.analyzeIncident(id));
    }

    @PostMapping("/{id}/analysis-requests")
    public ResponseEntity<Void> requestIncidentAnalysis(@PathVariable Long id ) {
        incidentService.requestIncidentAnalysis(id);
        return ResponseEntity.accepted().build();
    }
}
