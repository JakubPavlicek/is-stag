# Run to start minikube with Parallels:
#   minikube start --driver=parallels --cpus=10 --memory=16g --namespace=is-stag
# Run to start minikube with Docker:
#   minikube start --driver=docker --base-image='gcr.io/k8s-minikube/kicbase:v0.0.48' --namespace=is-stag

NAMESPACE=is-stag

# Add Helm repositories
helm repo add jetstack https://charts.jetstack.io
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add grafana https://grafana.github.io/helm-charts
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add open-telemetry https://open-telemetry.github.io/opentelemetry-helm-charts
helm repo add codecentric https://codecentric.github.io/helm-charts
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

# Create Namespace and apply basic configurations
kubectl apply -f infrastructure/is-stag-namespace.yaml
kubectl apply -f infrastructure/is-stag-database-config.yaml
kubectl apply -f infrastructure/is-stag-database-secret.yaml
kubectl apply -f infrastructure/keycloak-postgresql-secret.yaml
kubectl apply -f infrastructure/redis-secret.yaml
kubectl apply -f infrastructure/ingress.yaml

# ----- Cert-Manager for TLS certificates -----

helm upgrade --install cert-manager jetstack/cert-manager \
  --version v1.19.1 \
  --namespace $NAMESPACE \
  --set crds.enabled=true \
  --wait

echo "Waiting for cert-manager to be ready..."
kubectl wait --for=condition=available --timeout=120s deployment/cert-manager -n $NAMESPACE
kubectl wait --for=condition=available --timeout=120s deployment/cert-manager-cainjector -n $NAMESPACE
kubectl wait --for=condition=available --timeout=120s deployment/cert-manager-webhook -n $NAMESPACE

kubectl apply -f infrastructure/cert-issuer.yaml
kubectl apply -f infrastructure/certificate.yaml

# ----- Observability (Grafana, Loki, Prometheus, Tempo) -----

# Grafana
helm upgrade --install grafana grafana/grafana \
  -f infrastructure/grafana-values.yaml \
  --version 10.1.2 \
  --namespace $NAMESPACE \
  --wait
#  --set persistence.enabled=true \

# Loki
helm upgrade --install loki grafana/loki \
  -f infrastructure/loki-values.yaml \
  --version 6.44.0 \
  --namespace $NAMESPACE \
  --wait

# Prometheus
helm upgrade --install prometheus prometheus-community/prometheus \
  -f infrastructure/prometheus-values.yaml \
  --version 27.42.0 \
  --namespace $NAMESPACE \
  --wait

# Tempo
helm upgrade --install tempo grafana/tempo \
  -f infrastructure/tempo-values.yaml \
  --version 1.24.0 \
  --namespace $NAMESPACE \
  --wait

# ----- OpenTelemetry -----

helm upgrade --install opentelemetry-operator open-telemetry/opentelemetry-operator \
  -f infrastructure/opentelemetry-operator-values.yaml \
  --version 0.98.0 \
  --namespace $NAMESPACE \
  --wait

echo "Waiting for opentelemetry-operator to be ready..."
kubectl wait --for=condition=available --timeout=120s deployment/opentelemetry-operator -n $NAMESPACE
kubectl wait --for=create --timeout=120s service/opentelemetry-operator-webhook -n $NAMESPACE

kubectl apply -f infrastructure/otel-collector.yaml
kubectl apply -f infrastructure/otel-instrumentation.yaml

echo "Waiting for otel-collector to be ready..."
kubectl wait --for=condition=available --timeout=120s deployment/otel-collector -n $NAMESPACE

# ----- Keycloak with PostgreSQL -----

helm upgrade --install keycloak-postgresql bitnami/postgresql \
  -f infrastructure/keycloak-postgresql-values.yaml \
  --version 18.1.3 \
  --namespace $NAMESPACE \
  --wait

helm upgrade --install keycloak codecentric/keycloakx \
  -f infrastructure/keycloak-values.yaml \
  --version 7.1.4 \
  --namespace $NAMESPACE \
  --wait

#helm upgrade --install keycloak bitnami/keycloak \
#  -f infrastructure/keycloak-bitnami-values.yaml \
#  --version 25.2.0 \
#  --namespace $NAMESPACE \
#  --wait

# ----- Redis (Cache and Rate Limit) -----

helm upgrade --install redis-cache bitnami/redis \
  -f infrastructure/redis-values.yaml \
  --version 23.2.2 \
  --namespace $NAMESPACE \
  --wait

helm upgrade --install redis-rate-limiter bitnami/redis \
  -f infrastructure/redis-values.yaml \
  --version 23.2.2 \
  --namespace $NAMESPACE \
  --wait

# ----- NGINX Ingress Controller -----

helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
  --version 4.13.3 \
  --namespace $NAMESPACE \
  --wait

# Export External IP for Ingress
#   minikube tunnel
# Then put a record to the /etc/hots file like (root permission needed):
#   <EXTERNAL_IP> is-stag.cz

# ----- is-stag application -----

helm upgrade --install is-stag charts/is-stag-app \
  --namespace $NAMESPACE
