package com.example.incident_service.messaging.consumer;

import com.example.incident_service.messaging.KafkaTopics;
import com.example.incident_service.messaging.event.IncidentAnalyzedEvent;
import com.example.incident_service.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IncidentAnalyzedConsumer {

    private final IncidentService incidentService;

    @KafkaListener(topics = KafkaTopics.INCIDENT_ANALYZED)
    public void consume(IncidentAnalyzedEvent event) {
        incidentService.saveIncidentAnalysis(event);
        System.out.println(
                "Incident analysis persisted for incident: "
                        + event.incidentId()
        );
    }
}
