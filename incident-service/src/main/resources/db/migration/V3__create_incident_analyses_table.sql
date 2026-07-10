CREATE TABLE incident_analyses (
                                   id BIGSERIAL PRIMARY KEY,

                                   incident_id BIGINT NOT NULL,

                                   summary TEXT NOT NULL,
                                   severity VARCHAR(50) NOT NULL,
                                   category VARCHAR(100) NOT NULL,
                                   possible_root_cause TEXT,
                                   suggested_actions TEXT,
                                   postmortem_draft TEXT,

                                   created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_incident_analyses_incident
                                       FOREIGN KEY (incident_id)
                                           REFERENCES incidents(id)
                                           ON DELETE CASCADE
);

CREATE INDEX idx_incident_analyses_incident_id
    ON incident_analyses(incident_id);

CREATE INDEX idx_incident_analyses_created_at
    ON incident_analyses(created_at);