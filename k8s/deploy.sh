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

kubectl apply -f infrastructure/cert-issuer.yaml
kubectl apply -f infrastructure/certificate.yaml

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
