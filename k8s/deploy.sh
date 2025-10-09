kubectl apply -f infrastructure/is-stag-namespace.yaml
kubectl apply -f infrastructure/ingress.yaml

kubectl apply -f applications/is-stag-database-config.yaml
kubectl apply -f applications/is-stag-database-secret.yaml
kubectl apply -f applications/client.yaml
kubectl apply -f applications/api-gateway.yaml
kubectl apply -f applications/codelist-service.yaml
kubectl apply -f applications/student-service.yaml
kubectl apply -f applications/study-plan-service.yaml
kubectl apply -f applications/user-service.yaml