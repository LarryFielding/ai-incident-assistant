CREATE TABLE incidents (
                           id BIGSERIAL PRIMARY KEY,
                           title VARCHAR(200) NOT NULL,
                           description TEXT NOT NULL,
                           raw_logs TEXT,
                           service_name VARCHAR(100) NOT NULL,
                           environment VARCHAR(50) NOT NULL,
                           status VARCHAR(30) NOT NULL,
                           created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                           updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);