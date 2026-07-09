package com.example.incident_service.integration.ai;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class AiAnalysisClient {
    private final RestClient aiServiceRestClient;

    public AiAnalysisClient(RestClient aiServiceRestClient) {
        this.aiServiceRestClient = aiServiceRestClient;
    }

    public AiIncidentAnalysisResponse analyzeIncident(AiIncidentAnalysisRequest request) {
        return aiServiceRestClient
                .post()
                .uri("/analyze-incident")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AiIncidentAnalysisResponse.class);
        /*try {
            AiIncidentAnalysisResponse response = aiServiceRestClient
                    .post()
                    .uri("/analyze-incident")
                    .body(request)
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(AiIncidentAnalysisResponse.class);
            return response;
        } catch ( HttpClientErrorException.UnprocessableContent e) {
            // This logs the exact reason details sent by the remote server
            System.err.println("Validation failed: " + request.toString());
        } finally {
            return null;
        }*/
    }
}
