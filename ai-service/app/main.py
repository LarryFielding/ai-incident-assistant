from fastapi import FastAPI

from app.api.routes import router

app = FastAPI(
    title="AI Incident Assistant - AI Service",
    version="0.1.0"
)

# Uncomment to debug received request:

# @app.middleware("http")
# async def debug_request_body(request, call_next):
#     body = await request.body()
#
#     print("========== FASTAPI DEBUG REQUEST ==========")
#     print("METHOD:", request.method)
#     print("PATH:", request.url.path)
#     print("HEADERS:", dict(request.headers))
#     print("RAW BODY:", body.decode("utf-8", errors="replace"))
#     print("===========================================")
#
#     response = await call_next(request)
#     return response


app.include_router(router)