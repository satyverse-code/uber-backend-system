# Chaos Testing (Litmus)

## Install Litmus (once)

```bash
helm repo add litmuschaos https://litmuschaos.github.io/litmus-helm/
helm upgrade --install chaos litmuschaos/litmus -n chaos --create-namespace
kubectl create clusterrolebinding litmus-admin --clusterrole=cluster-admin --serviceaccount=chaos:litmus
```

## Service Account (namespace uber)
Some experiments require elevated permissions. Create a service account:

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: litmus-admin
  namespace: uber
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: litmus-admin-uber
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
  - kind: ServiceAccount
    name: litmus-admin
    namespace: uber
```

Apply it:

```bash
kubectl apply -f k8s/chaos/litmus-admin.yaml
```

## Experiments

- Orchestrator pod delete:
  ```bash
  kubectl apply -f k8s/chaos/orchestrator-pod-delete.yaml
  ```

- Booking pod delete:
  ```bash
  kubectl apply -f k8s/chaos/booking-pod-delete.yaml
  ```

- Kafka broker pod delete (Strimzi):
  ```bash
  kubectl apply -f k8s/chaos/kafka-broker-pod-delete.yaml
  ```

- Postgres network latency:
  ```bash
  kubectl apply -f k8s/chaos/postgres-network-latency.yaml
  ```

## What to observe
- Circuit breakers open (Resilience4j metrics)
- Retries/backoff behavior
- Booking failure rate and assignment latency panels
- Kafka consumer lag spikes
- Jaeger traces with error spans and increased durations

## Cleanup
```bash
kubectl delete -f k8s/chaos/orchestrator-pod-delete.yaml
kubectl delete -f k8s/chaos/booking-pod-delete.yaml
kubectl delete -f k8s/chaos/kafka-broker-pod-delete.yaml
kubectl delete -f k8s/chaos/postgres-network-latency.yaml
```
