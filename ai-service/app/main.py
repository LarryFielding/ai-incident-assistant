from fastapi import FastAPI

app = FastAPI(
    title="AI Incident Assistant - AI Service",
    version="0.1.0"
)


@app.get("/health")
def health_check():
    return {
        "status": "ok",
        "service": "ai-service"
    }