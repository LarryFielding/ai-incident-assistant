package com.example.incident_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "incident_analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false, length = 50)
    private String severity;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(name = "possible_root_cause", columnDefinition = "TEXT")
    private String possibleRootCause;

    @Column(name = "suggested_actions", columnDefinition = "TEXT")
    private String suggestedActions;

    @Column(name = "postmortem_draft", columnDefinition = "TEXT")
    private String postmortemDraft;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
