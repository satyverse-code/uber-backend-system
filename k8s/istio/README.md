# Istio install + mTLS baseline

## Install Istio (demo profile)

```bash
curl -L https://istio.io/downloadIstio | ISTIO_VERSION=1.22.0 sh -
cd istio-1.22.0
export PATH=$PWD/bin:$PATH
istioctl install -y --set profile=demo
```

## Enable sidecar injection in the app namespace
```bash
kubectl label namespace uber istio-injection=enabled --overwrite
```

## Apply STRICT mTLS and DestinationRules
```bash
kubectl apply -f k8s/istio/peer-authentication.yaml
kubectl apply -f k8s/istio/destination-rules.yaml
```

## Optional: expose via Istio IngressGateway
If you want to route traffic through Istio instead of NGINX, apply:
```bash
kubectl apply -f k8s/istio/ingress-gateway.yaml
kubectl apply -f k8s/istio/virtual-service-gateway.yaml
```

Notes:
- With STRICT mTLS, traffic between services is encrypted and enforced by Istio.
- Ensure namespace is labeled for injection so pods get the Envoy sidecar.
