from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    # General LLM settings
    use_llm: bool = False
    llm_provider: str = "google"  # "openai" or "google"

    # OpenAI settings
    openai_api_key: str | None = None
    llm_model: str = "gpt-4o-mini"

    # Google AI Studio settings
    google_api_key: str | None = None
    google_llm_model: str = "gemini-2.5-flash"

    # Kafka settings
    kafka_bootstrap_servers: str = "localhost:9092"
    analysis_requested_topic: str = "incident.analysis.requested"
    incident_analyzed_topic: str = "incident.analyzed"
    consumer_group_id: str = "ai-service-analysis-consumer"

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore"
    )


settings = Settings()