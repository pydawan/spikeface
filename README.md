### Java EE7 Stack using Groovy

* Groovy 2.4.7
* Apache Deltaspike 1.7
* JSF 2.2
* Primefaces 6.0
* hibernate 5.2.2
* Spring-data-jpa 4.2.8
* Prettyfaces 3.4.1
* picketlink authentication/authorization
* internationalization
* Wildfly 10.1

### Features

* compiles with groovy lang
* Wildfly ready
* Arquillian test cases
* Primefaces UI
* modified Primefaces data grid component to use request based pagination and sorting
* automatic scaffolding using spring data pagination and crud
* data grid is also scrollable on small devices and responsive
* all around responsive design
* request based views
* type checked routes
* pretty URLs
* mavenized

### Hibernate use latest version on wildfly

 * copy hibernate to wildfly server
```
mvn dependency:unpack
```

### running tests

* use maven profile arq-wildfly-managed
* run the test or tests: mvn test -Parq-wildfly-remote
 