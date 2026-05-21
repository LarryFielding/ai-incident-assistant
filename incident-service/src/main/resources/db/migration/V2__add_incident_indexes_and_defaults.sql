-- Add defensive default values to the existing timestamp columns
ALTER TABLE incidents ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE incidents ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

-- Create indexes for optimizing common application queries
CREATE INDEX idx_incidents_status ON incidents(status);
CREATE INDEX idx_incidents_service_name ON incidents(service_name);
CREATE INDEX idx_incidents_created_at ON incidents(created_at DESC);