# Week 7 — Observability: Metrics, Logs, and Traces

## What
- Add service metrics (Micrometer/Prometheus), structured logs, and distributed tracing (OpenTelemetry).
- Create Grafana dashboards per service and golden signals.
- Propagate trace context across services and Kafka.

## Why
- Reduce MTTR with actionable telemetry across the request path.
- Golden signals (latency, traffic, errors, saturation) drive SLOs.
- Traces reveal cross-service bottlenecks and retries.

## How (behind the scenes)
- Spring Boot Micrometer exposes /actuator/prometheus; Prometheus scrapes and Grafana visualizes.
- OpenTelemetry SDK auto-instruments HTTP/Kafka; W3C traceparent headers flow via Gateway → Booking → Orchestrator → Notification.
- Log correlation: logs include traceId/spanId to pivot between traces and logs.

## Architecture (Mermaid)
```mermaid
flowchart LR
  Client --> Gateway
  Gateway --> Booking
  Booking --> Orchestrator
  Orchestrator --> Notification
  subgraph Observability
    Prometheus
    Grafana
    Jaeger[Jaeger/Tempo]
  end
  Booking -->|metrics| Prometheus
  Orchestrator -->|metrics| Prometheus
  Notification -->|metrics| Prometheus
  Prometheus --> Grafana
  Gateway -.trace-> Jaeger
  Booking -.trace-> Jaeger
  Orchestrator -.trace-> Jaeger
  Notification -.trace-> Jaeger
```

## Learner outcomes
- Expose metrics and configure scrapes/dashboards.
- Instrument tracing and propagate context across async hops.
- Correlate logs with traces to debug failures quickly.

## Scenario Q&A
- Q: High p95 latency but normal CPU?
  - Why: downstream retries/timeouts or saturation elsewhere.
  - How: inspect trace waterfall, look for outlier spans and network waits.
- Q: Missing spans between services?
  - What: headers stripped or sampling too low.
  - How: ensure traceparent/baggage forwarded; raise sample rate in non-prod.
