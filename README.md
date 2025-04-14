# EAP8 Demo - CICD

> [!IMPORTANT]  
> Having the code and the CICD resources in the same repo but in different branch is not the right approach. It is used in this repo to simplify the deployment and configuration.

## OpenShift CICD

- Create namespaces:
```sh
oc new-project eap-sample-dev
oc new-project eap-sample-qa
oc new-project eap-sample-prod
oc new-project eap-sample-cicd

# Puller image permisions
oc policy add-role-to-user system:image-puller system:serviceaccount:eap-sample-dev:default -n eap-sample-cicd
```

- Deploy databases:
```sh
# DEV
oc new-app postgresql-ephemeral \
  -p DATABASE_SERVICE_NAME=eap-sample-db \
  -p POSTGRESQL_USER=develop \
  -p POSTGRESQL_PASSWORD=develop \
  -p POSTGRESQL_DATABASE=sample-db -n eap-sample-dev

# QA
oc new-app postgresql-ephemeral \
  -p DATABASE_SERVICE_NAME=eap-sample-db \
  -p POSTGRESQL_USER=qa \
  -p POSTGRESQL_PASSWORD=qa \
  -p POSTGRESQL_DATABASE=sample-db -n eap-sample-qa
# PROD
oc new-app postgresql-ephemeral \
  -p DATABASE_SERVICE_NAME=eap-sample-db \
  -p POSTGRESQL_USER=production \
  -p POSTGRESQL_PASSWORD=production \
  -p POSTGRESQL_DATABASE=sample-db -n eap-sample-prod
```

- Create secret for cicd (update git and argo credentials):
```sh
oc create secret generic eap-cicd-config \
  --from-literal GIT_USER="clbartolome" \
  --from-literal GIT_TOKEN="<update>" \
  --from-literal ARGOCD_SERVER="openshift-gitops-server.openshift-gitops.svc.cluster.local" \
  --from-literal ARGOCD_USERNAME="admin" \
  --from-literal ARGOCD_PASSWORD="<update>" \
  -n eap-sample-cicd
```

- Apply Tekton resources:
```sh
oc apply -f tekton
```

- Create ArgoCD appSet:
```sh
oc apply -f argo/eap-sample.yaml
```

- Review and run Tekton Pipeline in eap-sample-cicd