package com.example.incident_service.messaging.producer;

import com.example.incident_service.messaging.KafkaTopics;
import com.example.incident_service.messaging.event.IncidentAnalysisRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IncidentAnalysisRequestedProducer {

    private final KafkaTemplate<String, IncidentAnalysisRequestedEvent> kafkaTemplate;

    public void publish(IncidentAnalysisRequestedEvent event) {
        String key = event.incidentId().toString();

        kafkaTemplate.send(
                KafkaTopics.INCIDENT_ANALYSIS_REQUESTED,
                key,
                event
        );
    }
}
