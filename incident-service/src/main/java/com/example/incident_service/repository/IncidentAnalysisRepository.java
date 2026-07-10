package com.example.incident_service.repository;

import com.example.incident_service.entity.IncidentAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentAnalysisRepository extends JpaRepository<IncidentAnalysis, Long> {
    List<IncidentAnalysis> findByIncidentIdOrderByCreatedAtDesc(Long incidentId);
}
