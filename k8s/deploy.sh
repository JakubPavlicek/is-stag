# Run to start minikube with Parallels:
#   minikube start --driver=parallels --cpus=10 --memory=16g --namespace=is-stag
# Run to start minikube with Docker:
#   minikube start --driver=docker --base-image='gcr.io/k8s-minikube/kicbase:v0.0.48' --namespace=is-stag

# Run after starting minikube:
#   minikube addons enable ingress

NAMESPACE=is-stag

kubectl apply -f infrastructure/is-stag-namespace.yaml
kubectl apply -f infrastructure/ingress.yaml
kubectl apply -f infrastructure/is-stag-database-config.yaml
kubectl apply -f infrastructure/is-stag-database-secret.yaml

kubectl apply -f infrastructure/cert-issuer.yaml
kubectl apply -f infrastructure/certificate.yaml

helm upgrade --install cert-manager oci://quay.io/jetstack/charts/cert-manager \
  --version v1.19.1 \
  --namespace $NAMESPACE \
  --set crds.enabled=true

helm upgrade --install is-stag charts/is-stag-app \
  --namespace $NAMESPACE

# Run after to expose the External IP if using Docker driver:
#   sudo minikube tunnel