# Run to start minikube with Parallels:
#   minikube start --driver=parallels --cpus=10 --memory=16g --namespace=is-stag
# Run to start minikube with Docker:
#   minikube start --driver=docker --base-image='gcr.io/k8s-minikube/kicbase:v0.0.48' --namespace=is-stag

NAMESPACE=is-stag

kubectl apply -f infrastructure/is-stag-namespace.yaml
kubectl apply -f infrastructure/ingress.yaml
kubectl apply -f infrastructure/is-stag-database-config.yaml
kubectl apply -f infrastructure/is-stag-database-secret.yaml

# ----- Cert-Manager for TLS certificates -----

helm upgrade --install cert-manager oci://quay.io/jetstack/charts/cert-manager \
  --version v1.19.1 \
  --namespace $NAMESPACE \
  --set crds.enabled=true

echo "Waiting for cert-manager to be ready..."
kubectl wait --for=condition=available --timeout=120s deployment/cert-manager -n $NAMESPACE
kubectl wait --for=condition=available --timeout=120s deployment/cert-manager-cainjector -n $NAMESPACE
kubectl wait --for=condition=available --timeout=120s deployment/cert-manager-webhook -n $NAMESPACE

kubectl apply -f infrastructure/cert-issuer.yaml
kubectl apply -f infrastructure/certificate.yaml

# ----- OpenTelemetry -----

helm repo add open-telemetry https://open-telemetry.github.io/opentelemetry-helm-charts
helm upgrade --install opentelemetry-operator open-telemetry/opentelemetry-operator \
  --version 0.98.0 \
  --namespace $NAMESPACE \
  --set "manager.collectorImage.repository=otel/opentelemetry-collector-k8s" \
  --set admissionWebhooks.certManager.enabled=true \
  --set admissionWebhooks.timeoutSeconds=30

echo "Waiting for opentelemetry-operator to be ready..."
kubectl wait --for=condition=available --timeout=120s deployment/opentelemetry-operator -n $NAMESPACE

kubectl apply -f infrastructure/otel-collector.yaml
kubectl apply -f infrastructure/otel-instrumentation.yaml

echo "Waiting for otel-collector to be ready..."
kubectl wait --for=condition=available --timeout=120s deployment/otel-collector-collector -n $NAMESPACE

# ----- NGINX Ingress Controller -----

helm upgrade --install nginx-ingress oci://ghcr.io/nginx/charts/nginx-ingress \
  --version 2.3.1 \
  --namespace $NAMESPACE

# Export External IP for Ingress
#   minikube tunnel
# Then put a record to the /etc/hots file like (root permission needed):
#   <EXTERNAL_IP> is-stag.cz

# ----- is-stag application -----

helm upgrade --install is-stag charts/is-stag-app \
  --namespace $NAMESPACE
