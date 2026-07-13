ALTER TABLE incidents
    ADD COLUMN environment_name VARCHAR(255),
    ADD COLUMN incident_occurred_at TIMESTAMP WITH TIME ZONE;