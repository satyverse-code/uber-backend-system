# Weekly Run-and-Validate Guide (Week 0 → Week 12)

This guide lists, for each week:
- Prereqs/services to have installed or available
- How to run/apply weekly changes
- What to validate compared to the previous week
- Key code/config areas updated

Mermaid diagrams render natively on GitHub. If publishing elsewhere, enable Mermaid support.

---

## Week 0 — Fundamentals and Project Setup
- Prereqs
  - JDK 17+, Maven, Docker, kubectl, kind/minikube (or a K8s cluster)
- Run
  - Build: `mvn -q -DskipTests package`
  - Local run: start core services via Spring Boot run configs
- Validate
  - Health endpoints respond
  - Basic endpoints return 200
- Key Updates
  - Project scaffolding, base modules, basic controllers

## Week 1 — Core Services and APIs
- Prereqs
  - Same as Week 0
- Run
  - Start gateway, booking, orchestrator, notification services
- Validate
  - API endpoints (booking create, status) reachable via gateway
- Key Updates
  - REST endpoints, DTOs, controllers expanded

## Week 2 — Domain Modeling and Event Flow
- Prereqs
  - Same as Week 1; local Kafka (Docker) or cloud Kafka
- Run
  - Start Kafka (e.g., `docker compose up kafka zookeeper`)
  - Run services; ensure producer/consumer properties
- Validate (vs Week 1)
  - Booking emits events; downstream services consume
  - Event schemas stable; no deserialization errors
- Key Updates
  - Entities, repositories, basic Kafka producer/consumer wiring

## Week 3 — Persistence, Kafka Basics, and Events
- Prereqs
  - Postgres (Docker) + Kafka
- Run
  - Apply DB migrations; set JDBC env vars
  - Run services with Kafka topics created
- Validate (vs Week 2)
  - Data persists in DB; events persisted/consumed
  - Basic idempotency checks
- Key Updates
  - Persistence layer, transaction boundaries, Kafka config

## Week 4 — Orchestrator, Nearest Driver, and Business Metrics
- Prereqs
  - PostGIS-enabled Postgres
- Run
  - Orchestrator consumes `trip_created`; queries nearest driver
- Validate (vs Week 3)
  - `driver_assigned` events emitted
  - Micrometer counters increase
- Key Updates
  - Orchestrator flows, geospatial queries, counters

## Week 5 — Helm Packaging, Ingress, Autoscaling
- Prereqs
  - Helm, NGINX Ingress Controller, metrics-server
- Run
  - `helm install` per service with values
  - Configure Ingress hosts
- Validate (vs Week 4)
  - External access via Ingress
  - HPA scales with load
- Key Updates
  - Helm charts (Deployment/Service/Ingress), probes, resources, HPA

## Week 6 — GitOps with Argo CD
- Prereqs
  - Argo CD installed and logged in
- Run
  - Create Applications pointing to Helm charts and weekly branches
  - Sync to deploy
- Validate (vs Week 5)
  - Drift detected and reconciled by Argo CD
  - Promotion by changing targetRevision
- Key Updates
  - Argo CD app manifests and GitOps workflow

## Week 7 — Observability: Metrics, Logs, Traces
- Prereqs
  - Prometheus, Grafana, Jaeger/Tempo (or equivalent)
- Run
  - Enable Micrometer, scrape via Prometheus; set Grafana dashboards
  - Configure OpenTelemetry exporters
- Validate (vs Week 6)
  - Golden signals visible per service
  - Traces show end-to-end flow; logs contain traceId/spanId
- Key Updates
  - Actuator/metrics, dashboards, tracing config and headers propagation

## Week 8 — Kafka Reliability, Idempotency, Schema Evolution
- Prereqs
  - Kafka + (optional) Schema Registry
- Run
  - Configure partitions/keys; implement retry/ DLQ
  - Add idempotency store
- Validate (vs Week 7)
  - No duplicates in side-effects
  - Retry/DLQ flows on processing errors
- Key Updates
  - Consumer groups, partitioning strategy, DLQ, schema evolution

## Week 9 — Multi-Region and Global Routing
- Prereqs
  - Two regions (or simulated), MSK/Kafka in both, Route53/CloudFront
- Run
  - Deploy to both regions; MirrorMaker 2 for critical topics
  - Configure latency-based routing and health checks
- Validate (vs Week 8)
  - Regional failover works; replication lag monitored
  - Read-local/write-primary strategy verified
- Key Updates
  - Multi-region manifests, MM2 config, DNS routing

## Week 10 — Security, Mesh, Rollouts, Autoscaling, Analytics
- Prereqs
  - Istio installed; Redis (for rate limiting); Prometheus
- Run
  - Gateway JWT/JWKS and rate limits; STRICT mTLS baseline
  - Argo Rollouts canary with Prometheus AnalysisTemplate
  - S3 export feature flag; IRSA/Secrets for creds
- Validate (vs Week 9)
  - JWT scopes enforced; per-route limits
  - Canary promotion/rollback based on metrics
  - S3 events exported when flag enabled
- Key Updates
  - SecurityConfig, RateLimitConfig, Helm templates (rollouts, DR/VS), S3Exporter

## Week 11 — Chaos Engineering, Fault Injection, Load Testing
- Prereqs
  - Chaos Mesh or Litmus, k6 CLI, Prometheus/Grafana
- Run
  - Apply PodChaos and NetworkChaos; run k6 with thresholds
  - Follow RUNBOOK for abort/rollback conditions
- Validate (vs Week 10)
  - Errors/latency within error budget under chaos
  - Alerts fire and dashboards show expected patterns
- Key Updates
  - Chaos manifests, k6 scripts, runbooks, alert rules

## Week 12 — Standalone Prometheus, Custom Alerts, Nightly k6
- Prereqs
  - K8s cluster with permissions; GitHub Actions access
- Run
  - Apply Prometheus namespace/config/rules/deployment
  - Configure Grafana provisioning; enable nightly k6 workflow
- Validate (vs Week 11)
  - Custom alerts operational; dashboards populated
  - Nightly k6 artifacts uploaded; trends tracked
- Key Updates
  - Prometheus manifests, alert rules, GH Actions `k6-nightly.yml`, extended chaos

---

## General Validation Checklist (every week)
- Health: Pods Ready, probes passing
- Observability: Metrics scraped, traces linked, logs structured
- Reliability: Retries/timeouts sensible; no retry storms
- Security: Least privilege; secrets not in logs; mTLS (when applicable)
- Performance: p95 within budget; HPA/KEDA behavior observed

## Notes
- If Mermaid isn’t rendering in your docs site, enable the Mermaid plugin/extension.
- For cloud services (AWS S3, MSK, RDS): ensure credentials via IRSA or secrets, never commit secrets to Git.
