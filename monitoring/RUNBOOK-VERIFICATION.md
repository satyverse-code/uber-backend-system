# Week 10 Verification Runbook

Namespace: uber

## JWT + scopes + rate limits (Gateway)
- 401 without token:
  curl -i http://<gateway-host>/api/booking/health
- 403 without scope:
  curl -H "Authorization: Bearer <token-without-booking.read>" http://<gateway-host>/api/booking/health
- 200 with scope:
  curl -H "Authorization: Bearer <token-with-booking.read>" http://<gateway-host>/api/booking/health
- Per-route rate limit (expect 429):
  hey -z 10s -q 200 http://<gateway-host>/api/booking/health

## Service Monitors scraping
kubectl -n uber get servicemonitor
kubectl -n uber get endpoints -l app=gateway
# Check Prom targets UI -> up

## Argo Rollouts canary
kubectl argo rollouts get rollout <gateway-rollout-name> -n uber
kubectl argo rollouts dashboard

## Istio mTLS + policies
kubectl -n uber get peerauthentication default -o yaml
kubectl -n uber get destinationrule
kubectl -n uber get virtualservice
istioctl proxy-config cluster <pod> -n uber | grep -E "booking|orchestrator|notification"
istioctl proxy-config route <pod> -n uber | less

## S3 analytics export (if enabled)
# Enable in Helm values for booking:
# env.ANALYTICS_EXPORT_ENABLED=true
# env.ANALYTICS_S3_BUCKET=<bucket>
# Provide creds via IRSA or Secret
# Trigger a trip event and verify new objects under s3://<bucket>/trip-events/

## Grafana
- Import/verify dashboards:
  - Gateway Performance (uid: gateway-perf)
  - Gateway Canary vs Stable (uid: gateway-canary)
- Confirm panels show data; if empty, verify ServiceMonitor/Prom scrape and labels.
