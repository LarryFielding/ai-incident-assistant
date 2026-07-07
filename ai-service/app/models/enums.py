from enum import Enum


class Environment(str, Enum):
    LOCAL = "LOCAL"
    DEV = "DEV"
    QA = "QA"
    STAGING = "STAGING"
    PROD = "PROD"


class Severity(str, Enum):
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    CRITICAL = "CRITICAL"


class IncidentCategory(str, Enum):
    DATABASE = "DATABASE"
    NETWORK = "NETWORK"
    APPLICATION = "APPLICATION"
    INFRASTRUCTURE = "INFRASTRUCTURE"
    SECURITY = "SECURITY"
    PERFORMANCE = "PERFORMANCE"
    UNKNOWN = "UNKNOWN"