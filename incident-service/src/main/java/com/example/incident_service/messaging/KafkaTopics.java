package com.example.incident_service.messaging;

public final class KafkaTopics {
    public static final String INCIDENT_ANALYSIS_REQUESTED =
            "incident.analysis.requested";

    public static final String INCIDENT_ANALYZED =
            "incident.analyzed";

    private KafkaTopics() {
    }
}
