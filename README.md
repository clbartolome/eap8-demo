# EAP8 Demo

Forked from: https://github.com/RedHat-Middleware-Workshops/eap8-rhd-learning-path.git

> [!NOTE]  
> Branch **'cicd'** contains instructions to setup a CICD process to build and deploy this application in multiple environments using Tekton and ArgoCD.

> [!TIP]  
> Review [this article](https://developers.redhat.com/learning/learn:java:develop-modern-java-applications-jboss-eap-8/resource/resources:introduction-jboss-eap-8-red-hat-openshift) to undestand how JBoss EAP could be deploy as an image in OCP (followed in this repo examples).


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
