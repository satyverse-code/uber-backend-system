# Load Testing

This folder contains k6 scripts for exercising the booking → orchestrator → driver reservation flow.

## Prereqs
- Services running locally:
  - Booking (8080), Orchestrator (8090)
- Seeded drivers in Mumbai (default seed data)
- k6 installed: https://k6.io/docs/get-started/installation/

## Run
```bash
k6 run loadtest/k6/booking-orchestrator.js
```

## What to watch
- Grafana (http://localhost:3000): HTTP p95, errors, consumer lag, business counters
- Jaeger (http://localhost:16686): traces spanning orchestrator requests
- Kibana (http://localhost:5601): correlated JSON logs using traceId

## Thresholds
- p95 latency < 1s
- error rate < 10%

You can tune VUs/duration in the script `options` block.
