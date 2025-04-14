# EAP8 Demo - CICD

Forked from: https://github.com/RedHat-Middleware-Workshops/eap8-rhd-learning-path.git

## OpenShift Deploy

- Create a namespace

```sh
oc new-project eap-demo
```

- Create postgresql database

```sh
oc new-app postgresql-ephemeral \
  -p DATABASE_SERVICE_NAME=eap-sample-db \
  -p POSTGRESQL_USER=develop \
  -p POSTGRESQL_PASSWORD=develop \
  -p POSTGRESQL_DATABASE=sample-db

# Wait until database is running
oc get pods -w
```

- Deploy application in JBoss EAP 8 (JDK 17) using helm (https://github.com/jbossas/eap-charts/blob/eap8-dev/charts/eap8/README.md)

```sh
# Add helm repo
helm repo add jboss-eap https://jbossas.github.io/eap-charts/


helm install eap-sample -f .s2i.yaml jboss-eap/eap8 --dry-run
# Review builds and application
```

## OpenShift CICD

- Create namespaces:
```sh
oc new-project eap-sample-dev
oc new-project eap-sample-qa
oc new-project eap-sample-prod
oc new-project eap-sample-cicd
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
oc create secret generic hello-secret \
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



## Contacts APP

This is a simple CRUD application to manage contacts. It uses the following Jakarta EE 10 API's:

- https://jakarta.ee/specifications/restful-ws/[Jakarta RESTful Web Services]
- https://jakarta.ee/specifications/cdi/[Jakarta Contexts Dependency Injection]
- https://jakarta.ee/specifications/persistence/[Jakarta Persistence]
- https://jakarta.ee/specifications/bean-validation/[Jakarta Bean Validation]
- https://jakarta.ee/specifications/transactions/[Jakarta Transactions]

The client side is composed of HTML and JavaScript. It receives events from the server (server-sent events) to indicate
when a contact has been added, deleted or modified. The page should then be updated to reflect the changes.

You can also watch events from the http://127.0.0.1:8080/contacts/events.html page when the application is running.

- Building the application:

```sh
mvn clean verify
```

- Run tests:

```sh
mvn clean test
```

- Running the application in WildFly:

```sh
mvn clean wildfly:dev
```

Open a browser at the following URL: http://127.0.0.1:8080/contacts
