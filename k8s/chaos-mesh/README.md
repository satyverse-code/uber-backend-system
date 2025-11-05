# Chaos Mesh Experiments (Week 12)

Namespace: uber

## Prerequisites
- Install Chaos Mesh: https://chaos-mesh.org/docs/production-installation-using-helm/
- Grant permissions to operate in namespace `uber`.

## Experiments

### 1) PodKill: driver-service
File: pod-kill-driver.yaml

Apply:
```
kubectl apply -f k8s/chaos-mesh/pod-kill-driver.yaml
```

Expectation:
- Kill one driver-service pod for 30s. K8s restarts it; booking path continues, small latency spike, no data loss.

### 2) NetworkDelay: booking -> orchestrator
File: net-delay-booking-orchestrator.yaml

Apply:
```
kubectl apply -f k8s/chaos-mesh/net-delay-booking-orchestrator.yaml
```

Expectation:
- Inject ~2000ms latency to orchestrator pods. Booking should respect timeouts/retries; if exceeded, compensation kicks in.

## Stop experiments
```
kubectl delete -f k8s/chaos-mesh/pod-kill-driver.yaml
kubectl delete -f k8s/chaos-mesh/net-delay-booking-orchestrator.yaml
```
