from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    # OpenAI settings
    openai_api_key: str | None = None
    llm_model: str = "gpt-4o-mini"

    # Google AI Studio settings
    google_api_key: str | None = None
    google_llm_model: str = "gemini-2.5-flash"

    # General LLM settings
    llm_provider: str = "google"  # "openai" or "google"
    use_llm: bool = False

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore"
    )


settings = Settings()
