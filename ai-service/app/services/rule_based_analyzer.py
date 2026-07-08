from app.models.enums import Environment, IncidentCategory, Severity
from app.models.requests import IncidentAnalysisRequest
from app.models.responses import IncidentAnalysisResponse


def analyze_with_rules(request: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
    text = build_search_text(request)

    category = classify_category(text)
    severity = classify_severity(request.environment, category, text)

    return IncidentAnalysisResponse(
        summary=build_summary(request, category, severity),
        severity=severity,
        category=category,
        possible_root_cause=build_possible_root_cause(category),
        suggested_actions=build_suggested_actions(category),
        postmortem_draft=build_postmortem_draft(request, category, severity),
    )


def build_search_text(request: IncidentAnalysisRequest) -> str:
    return " ".join(
        [
            request.title or "",
            request.description or "",
            request.raw_logs or "",
            request.service_name or "",
            request.environment_name or "",
        ]
    ).lower()


def classify_category(text: str) -> IncidentCategory:
    if contains_any(text, ["database", "sql", "query", "connection pool", "timeout"]):
        return IncidentCategory.DATABASE

    if contains_any(text, ["unauthorized", "forbidden", "jwt", "token", "authentication"]):
        return IncidentCategory.SECURITY

    if contains_any(text, ["dns", "network", "connection refused", "connection reset"]):
        return IncidentCategory.NETWORK

    if contains_any(text, ["memory", "outofmemory", "heap", "cpu", "latency"]):
        return IncidentCategory.PERFORMANCE

    if contains_any(text, ["exception", "nullpointer", "stack trace", "error"]):
        return IncidentCategory.APPLICATION

    return IncidentCategory.UNKNOWN


def classify_severity(
    environment: Environment,
    category: IncidentCategory,
    text: str,
) -> Severity:
    if environment == Environment.PROD and contains_any(
        text, ["outage", "down", "unavailable"]
    ):
        return Severity.CRITICAL

    if environment == Environment.PROD and category in [
        IncidentCategory.DATABASE,
        IncidentCategory.SECURITY,
        IncidentCategory.INFRASTRUCTURE,
    ]:
        return Severity.HIGH

    if environment == Environment.PROD:
        return Severity.MEDIUM

    if environment in [Environment.STAGING, Environment.QA]:
        return Severity.MEDIUM

    return Severity.LOW


def contains_any(text: str, keywords: list[str]) -> bool:
    return any(keyword in text for keyword in keywords)


def build_summary(
    request: IncidentAnalysisRequest,
    category: IncidentCategory,
    severity: Severity,
) -> str:
    return (
        f"The incident affecting {request.service_name} was classified as "
        f"{category.value} with {severity.value} severity."
    )


def build_possible_root_cause(category: IncidentCategory) -> str:
    root_causes = {
        IncidentCategory.DATABASE: "The incident may be related to database connectivity, slow queries, or resource contention.",
        IncidentCategory.SECURITY: "The incident may be related to authentication, authorization, or token validation.",
        IncidentCategory.NETWORK: "The incident may be related to network connectivity, DNS, or unreachable dependencies.",
        IncidentCategory.PERFORMANCE: "The incident may be related to high resource usage, latency, memory, or CPU pressure.",
        IncidentCategory.APPLICATION: "The incident may be caused by an application error or unexpected exception.",
        IncidentCategory.INFRASTRUCTURE: "The incident may be related to infrastructure availability or platform-level failures.",
        IncidentCategory.UNKNOWN: "The root cause is unclear based on the available information.",
    }

    return root_causes[category]


def build_suggested_actions(category: IncidentCategory) -> list[str]:
    actions = {
        IncidentCategory.DATABASE: [
            "Review database connection pool metrics.",
            "Check slow queries and database timeout logs.",
            "Validate database availability and recent schema changes.",
        ],
        IncidentCategory.SECURITY: [
            "Review authentication and authorization logs.",
            "Validate token configuration and expiration policies.",
            "Check recent security-related deployments or configuration changes.",
        ],
        IncidentCategory.NETWORK: [
            "Check DNS resolution and network connectivity.",
            "Validate upstream service availability.",
            "Review connection timeout and reset errors.",
        ],
        IncidentCategory.PERFORMANCE: [
            "Review CPU, memory, and latency metrics.",
            "Check recent traffic spikes or batch jobs.",
            "Analyze application performance traces.",
        ],
        IncidentCategory.APPLICATION: [
            "Review stack traces and application logs.",
            "Identify recent deployments or code changes.",
            "Reproduce the failing scenario in a lower environment.",
        ],
        IncidentCategory.INFRASTRUCTURE: [
            "Check infrastructure health and platform alerts.",
            "Review container, node, or cluster-level events.",
            "Validate recent infrastructure changes.",
        ],
        IncidentCategory.UNKNOWN: [
            "Collect more logs and metrics.",
            "Identify recent deployments or configuration changes.",
            "Correlate the incident timeline with system alerts.",
        ],
    }

    return actions[category]


def build_postmortem_draft(
    request: IncidentAnalysisRequest,
    category: IncidentCategory,
    severity: Severity,
) -> str:
    return (
        f"Postmortem draft for incident in {request.service_name}. "
        f"The incident occurred in {request.environment.value} and was classified "
        f"as {category.value} with {severity.value} severity. "
        f"Initial analysis suggests that the issue may be related to: "
        f"{build_possible_root_cause(category)} "
        f"Further investigation is required to confirm the root cause, assess impact, "
        f"and define preventive actions."
    )