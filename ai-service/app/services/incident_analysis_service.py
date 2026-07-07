from app.models.enums import Environment, IncidentCategory, Severity
from app.models.requests import IncidentAnalysisRequest
from app.models.responses import IncidentAnalysisResponse


def analyze_incident(request: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
    incident_text = _build_incident_text(request)

    category = _classify_category(incident_text)
    severity = _classify_severity(request.environment, incident_text, category)

    return IncidentAnalysisResponse(
        summary=_build_summary(request, category, severity),
        severity=severity,
        category=category,
        possible_root_cause=_build_possible_root_cause(category),
        suggested_actions=_build_suggested_actions(category),
        postmortem_draft=_build_postmortem_draft(request, category, severity)
    )


def _build_incident_text(request: IncidentAnalysisRequest) -> str:
    parts = [
        request.title,
        request.description,
        request.raw_logs or ""
    ]

    return " ".join(parts).lower()


def _classify_category(incident_text: str) -> IncidentCategory:
    if any(keyword in incident_text for keyword in ["database", "sql", "query", "connection pool", "timeout"]):
        return IncidentCategory.DATABASE

    if any(keyword in incident_text for keyword in ["unauthorized", "forbidden", "jwt", "token", "authentication"]):
        return IncidentCategory.SECURITY

    if any(keyword in incident_text for keyword in ["dns", "network", "connection refused", "connection reset"]):
        return IncidentCategory.NETWORK

    if any(keyword in incident_text for keyword in ["memory", "outofmemory", "heap", "cpu", "latency"]):
        return IncidentCategory.PERFORMANCE

    if any(keyword in incident_text for keyword in ["exception", "nullpointer", "stack trace", "error"]):
        return IncidentCategory.APPLICATION

    return IncidentCategory.UNKNOWN


def _classify_severity(
    environment: Environment,
    incident_text: str,
    category: IncidentCategory
) -> Severity:
    if environment == Environment.PROD and any(keyword in incident_text for keyword in ["outage", "down", "unavailable"]):
        return Severity.CRITICAL

    if environment == Environment.PROD and category in [
        IncidentCategory.DATABASE,
        IncidentCategory.SECURITY,
        IncidentCategory.INFRASTRUCTURE
    ]:
        return Severity.HIGH

    if environment == Environment.PROD:
        return Severity.MEDIUM

    if environment in [Environment.STAGING, Environment.QA]:
        return Severity.MEDIUM

    return Severity.LOW


def _build_summary(
    request: IncidentAnalysisRequest,
    category: IncidentCategory,
    severity: Severity
) -> str:
    return (
        f"Incident reported for service {request.service_name} in {request.environment.value}. "
        f"The incident was classified as {category.value} with {severity.value} severity."
    )


def _build_possible_root_cause(category: IncidentCategory) -> str:
    root_causes = {
        IncidentCategory.DATABASE: "The issue may be related to database connectivity, slow queries, or connection pool saturation.",
        IncidentCategory.SECURITY: "The issue may be related to authentication, authorization, or token validation failures.",
        IncidentCategory.NETWORK: "The issue may be related to network connectivity, DNS resolution, or service-to-service communication failures.",
        IncidentCategory.PERFORMANCE: "The issue may be related to resource saturation, memory pressure, CPU usage, or increased latency.",
        IncidentCategory.APPLICATION: "The issue may be related to an application error, unhandled exception, or recent code change.",
        IncidentCategory.INFRASTRUCTURE: "The issue may be related to infrastructure availability or platform-level problems.",
        IncidentCategory.UNKNOWN: "The available information is not enough to determine a clear root cause."
    }

    return root_causes[category]


def _build_suggested_actions(category: IncidentCategory) -> list[str]:
    actions = {
        IncidentCategory.DATABASE: [
            "Check database connection pool metrics.",
            "Review slow query logs during the incident window.",
            "Verify recent schema, query, or configuration changes."
        ],
        IncidentCategory.SECURITY: [
            "Review authentication and authorization logs.",
            "Validate token expiration, signing keys, and permissions.",
            "Check whether recent security configuration changes were deployed."
        ],
        IncidentCategory.NETWORK: [
            "Check DNS resolution and network connectivity between services.",
            "Review load balancer and gateway logs.",
            "Verify whether dependent services are reachable."
        ],
        IncidentCategory.PERFORMANCE: [
            "Review CPU, memory, and latency metrics.",
            "Check for traffic spikes during the incident window.",
            "Inspect recent deployments that may have increased resource usage."
        ],
        IncidentCategory.APPLICATION: [
            "Review application logs and stack traces.",
            "Check recent deployments or code changes.",
            "Add defensive handling around the failing execution path."
        ],
        IncidentCategory.INFRASTRUCTURE: [
            "Check infrastructure health metrics.",
            "Review platform or container restart events.",
            "Verify availability of dependent infrastructure components."
        ],
        IncidentCategory.UNKNOWN: [
            "Collect additional logs and metrics.",
            "Identify recent changes around the incident window.",
            "Validate whether the issue is reproducible."
        ]
    }

    return actions[category]


def _build_postmortem_draft(
    request: IncidentAnalysisRequest,
    category: IncidentCategory,
    severity: Severity
) -> str:
    return (
        f"An incident occurred in service {request.service_name} on "
        f"{request.incident_occurred_at.isoformat()}. "
        f"The incident affected the {request.environment.value} environment and was classified as "
        f"{category.value} with {severity.value} severity. "
        "Initial analysis suggests a probable technical issue based on the incident description and logs. "
        "Further investigation is required to confirm the root cause, customer impact, and final remediation steps."
    )